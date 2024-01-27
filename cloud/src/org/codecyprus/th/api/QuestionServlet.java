package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ably.lib.util.Log;
import org.codecyprus.th.db.ConfiguredQuestionFactory;
import org.codecyprus.th.db.QuestionFactory;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.db.TreasureHuntFactory;
import org.codecyprus.th.model.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

public class QuestionServlet extends HttpServlet {

    public static final String PARAMETER_SESSION = "session";
    public static final String PARAMETER_SECRET_CODE = "code";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final String sessionId = request.getParameter(PARAMETER_SESSION);
        final String secretCode = request.getParameter(PARAMETER_SECRET_CODE);

        if(sessionId == null || sessionId.trim().isEmpty()) {
            // check for errors/missing parameters
            final Replies.ErrorReply errorReply = new Replies.ErrorReply("Missing or empty parameter: " + PARAMETER_SESSION);
            printWriter.println(gson.toJson(errorReply));
        } else {
            final Session session = SessionFactory.getSession(sessionId);

            if(session == null) {
                final Replies.ErrorReply errorReply = new Replies.ErrorReply("Unknown session. The specified session ID could not be found.");
                printWriter.println(gson.toJson(errorReply));
            } else {
                // first retrieve corresponding treasure hunt...
                final TreasureHunt treasureHunt = TreasureHuntFactory.getTreasureHunt(session.getTreasureHuntUuid());
                assert treasureHunt != null;
                final boolean treasureHuntIsActive = treasureHunt.isActiveNow();
                final boolean secretKeyIsValid = secretCode != null && secretCode.equals(treasureHunt.getSecretCode()); // the secretCode enables to bypass inactive THs
                if (!treasureHuntIsActive && !secretKeyIsValid) {
                    final String errorMessage = treasureHunt.isNotStarted() ?
                            "This Treasure Hunt has not started yet." :
                            treasureHunt.isFinished() ? "This Treasure Hunt has already ended." :
                            "This Treasure Hunt is not active right now";
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply(errorMessage);
                    printWriter.println(gson.toJson(errorReply));
                } else {
                    final ArrayList<String> configuredQuestionUuids = session.getConfiguredQuestionUuids();
                    final int numOfQuestions = configuredQuestionUuids.size();
                    final int currentConfiguredQuestionIndex = session.getCurrentConfiguredQuestionIndex().intValue();
                    if(currentConfiguredQuestionIndex >= numOfQuestions) {
                        final Replies.QuestionReply reply = new Replies.QuestionReply(true, "No more unanswered questions", QuestionType.TEXT, false, false, numOfQuestions, currentConfiguredQuestionIndex, 0, 0, 0);
                        printWriter.println(gson.toJson(reply));
                    } else {
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
                                final Replies.QuestionReply reply = new Replies.QuestionReply(false, question.getQuestionText(), question.getQuestionType(), configuredQuestion.isCanBeSkipped(), configuredQuestion.isLocationRelevant(), numOfQuestions, currentConfiguredQuestionIndex, configuredQuestion.getCorrectScore(), configuredQuestion.getWrongScore(), configuredQuestion.getSkipScore());
                                printWriter.println(gson.toJson(reply));
                            }
                        }
                    }
                }
            }
        }
    }
}
