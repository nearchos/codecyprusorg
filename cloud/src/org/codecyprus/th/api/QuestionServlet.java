package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.codecyprus.th.db.ConfiguredQuestionFactory;
import org.codecyprus.th.db.QuestionFactory;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.model.ConfiguredQuestion;
import org.codecyprus.th.model.Question;
import org.codecyprus.th.model.QuestionType;
import org.codecyprus.th.model.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

public class QuestionServlet extends HttpServlet {

    public static final String PARAMETER_SESSION = "session";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String sessionId = request.getParameter(PARAMETER_SESSION);

        if(sessionId == null || sessionId.trim().isEmpty()) {
            // check for errors/missing parameters
            final ErrorReply errorReply = new ErrorReply("Missing or empty parameter: " + PARAMETER_SESSION);
            printWriter.println(gson.toJson(errorReply));
        } else {
            final Session session = SessionFactory.getSession(sessionId);

            if(session == null) {
                final ErrorReply errorReply = new ErrorReply("Unknown session. The specified session ID could not be found.");
                printWriter.println(gson.toJson(errorReply));
            } else {
                final ArrayList<String> configuredQuestionUuids = session.getConfiguredQuestionUuids();
                final int numOfQuestions = configuredQuestionUuids.size();
                final int currentConfiguredQuestionIndex = session.getCurrentConfiguredQuestionIndex().intValue();
                if(currentConfiguredQuestionIndex >= numOfQuestions) {
                    final Reply reply = new Reply(true, "No more unanswered questions", QuestionType.TEXT, false, false, numOfQuestions, currentConfiguredQuestionIndex);
                    printWriter.println(gson.toJson(reply));
                } else {
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
                            final Reply reply = new Reply(false, question.getQuestionText(), question.getQuestionType(), configuredQuestion.isCanBeSkipped(), configuredQuestion.isLocationRelevant(), numOfQuestions, currentConfiguredQuestionIndex);
                            printWriter.println(gson.toJson(reply));
                        }
                    }
                }
            }
        }

    }

    public class Reply implements Serializable {

        private String status = "OK";

        private boolean completed;

        @SerializedName("question-text")
        private String questionText;

        @SerializedName("question-type")
        private QuestionType questionType;

        @SerializedName("can-be-skipped")
        private boolean canBeSkipped;

        @SerializedName("requires-location")
        private boolean requiresLocation;

        @SerializedName("num-of-questions")
        private int numOfQuestions;

        @SerializedName("current-question-index")
        private int currentQuestionIndex;

        public Reply(boolean completed, String questionText, QuestionType questionType, boolean canBeSkipped, boolean requiresLocation, int numOfQuestions, int currentQuestionIndex) {
            this.completed = completed;
            this.questionText = questionText;
            this.questionType = questionType;
            this.canBeSkipped = canBeSkipped;
            this.requiresLocation = requiresLocation;
            this.numOfQuestions = numOfQuestions;
            this.currentQuestionIndex = currentQuestionIndex;
        }
    }
}