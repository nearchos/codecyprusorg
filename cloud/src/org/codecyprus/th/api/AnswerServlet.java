package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ably.lib.rest.AblyRest;
import io.ably.lib.rest.Channel;
import io.ably.lib.types.AblyException;
import org.codecyprus.th.db.*;
import org.codecyprus.th.model.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class AnswerServlet extends HttpServlet {

    public static final String PARAMETER_SESSION = "session";
    public static final String PARAMETER_ANSWER = "answer";

    public static final long TIME_THRESHOLD = 2L * 60 * 1000; // 2 minutes

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final ArrayList<String> errorMessages = new ArrayList<>();

        final String sessionUuid = request.getParameter(PARAMETER_SESSION);
        final String answer = request.getParameter(PARAMETER_ANSWER);

        if(sessionUuid == null || sessionUuid.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_SESSION);
        }
        if(answer == null || answer.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_ANSWER);
        }

        if(!errorMessages.isEmpty()) {
            final Replies.ErrorReply errorReply = new Replies.ErrorReply(errorMessages);
            printWriter.println(gson.toJson(errorReply));
        } else {
            assert answer != null;
            final Session session = SessionFactory.getSession(sessionUuid);
            if(session == null) {
                final Replies.ErrorReply errorReply = new Replies.ErrorReply("Unknown session. The specified session ID could not be found.");
                printWriter.println(gson.toJson(errorReply));
            } else {
                // check if session is active
                if (session.isCompleted()) {
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply("Completed session. The specified session has no more unanswered questions.");
                    printWriter.println(gson.toJson(errorReply));
                } else if (session.isFinished()) {
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply("Finished session. The specified session has run out of time.");
                    printWriter.println(gson.toJson(errorReply));
                } else {
                    final ArrayList<String> configuredQuestionUuids = session.getConfiguredQuestionUuids();
                    final int currentConfiguredQuestionIndex = session.getCurrentConfiguredQuestionIndex().intValue();

                    final String currentConfiguredQuestionUuid = configuredQuestionUuids.get(currentConfiguredQuestionIndex);
                    final ConfiguredQuestion configuredQuestion = ConfiguredQuestionFactory.getConfiguredQuestion(currentConfiguredQuestionUuid);

                    if(configuredQuestion == null) {
                        final Replies.ErrorReply errorReply = new Replies.ErrorReply("Internal error. Could not find ConfiguredQuestion for uuid: " + currentConfiguredQuestionUuid);
                        printWriter.println(gson.toJson(errorReply));
                    } else {
                        final Question question = QuestionFactory.getQuestion(configuredQuestion.getQuestionUuid());
                        if(question == null) {
                            final Replies.ErrorReply errorReply = new Replies.ErrorReply("Internal error. Could not find Question for uuid: " + configuredQuestion.getQuestionUuid());
                            printWriter.println(gson.toJson(errorReply));
                        } else {
                            final boolean correct = processAnswer(session, question, answer);
                            if(!correct) { // answer is incorrect
                                final int scoreAdjustment = configuredQuestion.getWrongScore().intValue();
                                SessionFactory.updateSession(session, scoreAdjustment);
                                // todo add to history
                                final Replies.AnswerReply reply = new Replies.AnswerReply(false, false, "Wrong answer: " + answer, scoreAdjustment);
                                printWriter.println(gson.toJson(reply));
                            } else { // answer is correct
                                // first check if location is relevant
                                if(configuredQuestion.isLocationRelevant()) {
                                    final Location location = LocationFactory.getLatestLocation(sessionUuid); // get latest location fingerprint
                                    if(location == null) { // no location fingerprints in record
                                        final Replies.AnswerReply reply = new Replies.AnswerReply(false, false, "This is a location-sensitive question but no location records are found for player: " + session.getPlayerName(), 0);
                                        printWriter.println(gson.toJson(reply));
                                    } else if(location.getTimestamp() + TIME_THRESHOLD < System.currentTimeMillis()) { // the latest location is too old
                                        final Replies.AnswerReply reply = new Replies.AnswerReply(false, false, "This is a location-sensitive question but there is no recent location recorded for you. Latest one is from: " + new Date(location.getTimestamp()) + ".", 0);
                                        printWriter.println(gson.toJson(reply));
                                    } else {
                                        final double actualDistance = location.distanceTo(configuredQuestion.getLatitude(), configuredQuestion.getLongitude());
                                        final double threshold = configuredQuestion.getDistanceThreshold();
                                        if(actualDistance > threshold) {
                                            // too far
                                            final String formattedActualDistance = actualDistance < 1000 ?
                                                    String.format("%d meters", (Math.round(actualDistance))) :
                                                    String.format("%.1f kilometers", actualDistance / 1000);
                                            final Replies.AnswerReply reply = new Replies.AnswerReply(false, false, "This is a location-sensitive question and your current location appears to be " + formattedActualDistance + " from the target which is further than the limit of " + threshold + " meters.", 0);
                                            printWriter.println(gson.toJson(reply));
                                        } else { // ok, location is fine
                                            final int scoreAdjustment = configuredQuestion.getCorrectScore().intValue();
                                            final boolean completed = SessionFactory.updateSessionAndAdvance(session, scoreAdjustment);
                                            // todo add to history
                                            final Replies.AnswerReply reply = new Replies.AnswerReply(true, completed, "Well done.", scoreAdjustment);
                                            printWriter.println(gson.toJson(reply));
                                        }
                                    }
                                } else { // location is not relevant, so just handle this as correct answer
                                    final int scoreAdjustment = configuredQuestion.getCorrectScore().intValue();
                                    final boolean completed = SessionFactory.updateSessionAndAdvance(session, scoreAdjustment);
                                    // todo add to history
                                    final Replies.AnswerReply reply = new Replies.AnswerReply(true, completed, "Well done.", scoreAdjustment);
                                    printWriter.println(gson.toJson(reply));
                                }
                            }
                            // ably push
                            final Session updatedSession = SessionFactory.getSession(sessionUuid);
                            assert updatedSession != null;
                            final Location location = LocationFactory.getLatestLocation(updatedSession.getUuid());
                            final double latitude = location == null ? 0d : location.getLatitude();
                            final double longitude = location == null ? 0d : location.getLongitude();
                            pushAblyAnswer(updatedSession.getTreasureHuntUuid(),
                                    new AblyUpdate(updatedSession.getUuid(), updatedSession.getAppName(), updatedSession.getPlayerName(), updatedSession.getScore(), updatedSession.getCompletionTime(), latitude, longitude));
                        }
                    }
                }
            }
        }
    }

    /** Indicates that any answer provided by player is considered correct */
    private static final String SPECIAL_ALWAYS_CORRECT = "%any%";

    /** Indicates that the question must somehow match a code challenge, based on a hash of the team and a unique code handed out before the treasure hunt */
    private static final String SPECIAL_TEAM_CODE_CHALLENGE = "%teamChallenge%";

    private boolean processAnswer(final Session session, final Question question, final String answer) {
        final String correctAnswer = question.getCorrectAnswer();

        if(SPECIAL_ALWAYS_CORRECT.equals(correctAnswer)) {
            return true;
        } else if(SPECIAL_TEAM_CODE_CHALLENGE.equalsIgnoreCase(correctAnswer)) {
            assert session != null;
            final String playerName = session.getPlayerName() == null ? "" : session.getPlayerName().toLowerCase().trim();
            final String treasureHuntUUID = session.getTreasureHuntUuid();
            final TreasureHunt treasureHunt = TreasureHuntFactory.getTreasureHunt(treasureHuntUUID);
            // assert TreasureHunt is not null
            assert treasureHunt != null;
            try {
                final String secretCode = treasureHunt.getSecretCode();
                String code = md5(playerName + secretCode);
                code = code.substring(code.length() > 4 ? code.length() - 4 : 0);
                return answer.equalsIgnoreCase(code);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                // todo log error
                return false;
            }
        } else {
            return answer.trim().equalsIgnoreCase(correctAnswer.trim());
        }
    }

    private void pushAblyAnswer(final String treasureHuntUUID, final AblyUpdate ablyUpdate) {
        try {
            final Parameter parameter = ParameterFactory.getParameter("ABLY_PRIVATE_KEY");
            if(parameter != null) {
                final String ablyKey = parameter.getValue();
                final AblyRest ably = new AblyRest(ablyKey);
                final Channel channel = ably.channels.get("th-" + treasureHuntUUID);
                final String json = gson.toJson(ablyUpdate);
                io.ably.lib.types.Message[] messages = new io.ably.lib.types.Message[]{new io.ably.lib.types.Message("session_update", json)};
                channel.publish(messages);
            }
        } catch (AblyException ae) {
            log.severe("Ably error: " + ae.errorInfo);
        }
    }

    private static String convertToHex(byte[] data) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    stringBuilder.append((char) ('0' + halfbyte));
                } else {
                    stringBuilder.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return stringBuilder.toString();
    }

    public static String md5(String text, int sliceRight) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return md5(text).substring(text.length() - sliceRight);
    }

    public static String md5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md = MessageDigest.getInstance("MD5");
        byte [] md5hash = new byte[32];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }
}