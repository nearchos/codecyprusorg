package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.codecyprus.th.db.ConfiguredQuestionFactory;
import org.codecyprus.th.db.LocationFactory;
import org.codecyprus.th.db.QuestionFactory;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.model.ConfiguredQuestion;
import org.codecyprus.th.model.Location;
import org.codecyprus.th.model.Question;
import org.codecyprus.th.model.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
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
            final ErrorReply errorReply = new ErrorReply(errorMessages);
            printWriter.println(gson.toJson(errorReply));
        } else {
            assert answer != null;
            final Session session = SessionFactory.getSession(sessionUuid);
            if(session == null) {
                final ErrorReply errorReply = new ErrorReply("Unknown session. The specified session ID could not be found.");
                printWriter.println(gson.toJson(errorReply));
            } else {
                // check if session is active
                if (session.isCompleted()) {
                    final ErrorReply errorReply = new ErrorReply("Completed session. The specified session has no more unanswered questions.");
                    printWriter.println(gson.toJson(errorReply));
                } else if (session.isFinished()) {
                    final ErrorReply errorReply = new ErrorReply("Finished session. The specified session has run out of time.");
                    printWriter.println(gson.toJson(errorReply));
                } else {
                    final ArrayList<String> configuredQuestionUuids = session.getConfiguredQuestionUuids();
                    final int currentConfiguredQuestionIndex = session.getCurrentConfiguredQuestionIndex().intValue();

                    final String currentConfiguredQuestionUuid = configuredQuestionUuids.get(currentConfiguredQuestionIndex);
                    final ConfiguredQuestion configuredQuestion = ConfiguredQuestionFactory.getConfiguredQuestion(currentConfiguredQuestionUuid);

                    if(configuredQuestion == null) {
                        final ErrorReply errorReply = new ErrorReply("Internal error. Could not find ConfiguredQuestion for uuid: " + currentConfiguredQuestionUuid);
                        printWriter.println(gson.toJson(errorReply));
                    } else {
                        final Question question = QuestionFactory.getQuestion(configuredQuestion.getQuestionUuid());
                        if(question == null) {
                            final ErrorReply errorReply = new ErrorReply("Internal error. Could not find Question for uuid: " + configuredQuestion.getQuestionUuid());
                            printWriter.println(gson.toJson(errorReply));
                        } else {
                            final String correctAnswer = question.getCorrectAnswer();
                            final boolean correct = answer.trim().equalsIgnoreCase(correctAnswer.trim());
                            if(!correct) { // answer is incorrect
                                final int scoreAdjustment = configuredQuestion.getWrongScore().intValue();
                                SessionFactory.updateSession(session, scoreAdjustment);
                                // todo add to history
                                final Reply reply = new Reply(false, false, "Wrong answer: " + answer, scoreAdjustment);
                                printWriter.println(gson.toJson(reply));
                            } else { // answer is correct
                                // first check if location is relevant
                                if(configuredQuestion.isLocationRelevant()) {
                                    final Location location = LocationFactory.getLatestLocation(sessionUuid); // get latest location fingerprint
                                    if(location == null) { // no location fingerprints in record
                                        final Reply reply = new Reply(false, false, "This is a location-sensitive question but no location fingerprint is found for session: " + session.getUuid(), 0);
                                        printWriter.println(gson.toJson(reply));
                                    } else if(location.getTimestamp() + TIME_THRESHOLD < System.currentTimeMillis()) { // the latest location is too old
                                        final Reply reply = new Reply(false, false, "This is a location-sensitive question but there is no recent fingerprint. Latest one is from: " + new Date(location.getTimestamp()) + ".", 0);
                                        printWriter.println(gson.toJson(reply));
                                    } else {
                                        if(location.distanceTo(configuredQuestion.getLatitude(), configuredQuestion.getLongitude()) > configuredQuestion.getDistanceThreshold()) {
                                            // too far
                                            final Reply reply = new Reply(false, false, "This is a location-sensitive question and your current location appears to be further than: " + configuredQuestion.getDistanceThreshold() + " meters from the intended target.", 0);
                                            printWriter.println(gson.toJson(reply));
                                        } else { // ok, location is fine
                                            final int scoreAdjustment = configuredQuestion.getCorrectScore().intValue();
                                            final boolean completed = SessionFactory.updateSessionAndAdvance(session, scoreAdjustment);
                                            // todo add to history
                                            final Reply reply = new Reply(true, completed, "Well done.", scoreAdjustment);
                                            printWriter.println(gson.toJson(reply));
                                        }
                                    }
                                } else { // location is not relevant, so just handle this as correct answer
                                    final int scoreAdjustment = configuredQuestion.getCorrectScore().intValue();
                                    final boolean completed = SessionFactory.updateSessionAndAdvance(session, scoreAdjustment);
                                    // todo add to history
                                    final Reply reply = new Reply(true, completed, "Well done.", scoreAdjustment);
                                    printWriter.println(gson.toJson(reply));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public class Reply implements Serializable {

        private String status = "OK";
        private boolean correct;
        private boolean completed;
        private String message;
        @SerializedName("scoreAdjustment")
        private int scoreAdjustment;

        public Reply(boolean correct, boolean completed, String message, int scoreAdjustment) {
            this.correct = correct;
            this.completed = completed;
            this.message = message;
            this.scoreAdjustment = scoreAdjustment;
        }
    }
}
