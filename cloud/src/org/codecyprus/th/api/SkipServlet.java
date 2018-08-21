package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.codecyprus.th.db.ConfiguredQuestionFactory;
import org.codecyprus.th.db.QuestionFactory;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.model.ConfiguredQuestion;
import org.codecyprus.th.model.Question;
import org.codecyprus.th.model.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SkipServlet extends HttpServlet {

    public static final String PARAMETER_SESSION = "session";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final ArrayList<String> errorMessages = new ArrayList<>();

        final String sessionUuid = request.getParameter(PARAMETER_SESSION);

        if(sessionUuid == null || sessionUuid.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_SESSION);
        }

        if(!errorMessages.isEmpty()) {
            final ErrorReply errorReply = new ErrorReply(errorMessages);
            printWriter.println(gson.toJson(errorReply));
        } else {
            final Session session = SessionFactory.getSession(sessionUuid);
            if(session == null) {
                final ErrorReply errorReply = new ErrorReply("Unknown session. The specified session ID could not be found.");
                printWriter.println(gson.toJson(errorReply));
            } else {
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
                        if(!configuredQuestion.isCanBeSkipped()) {
                            final ErrorReply errorReply = new ErrorReply("Cannot skip. This questions is defined as one that cannot be skipped.");
                            printWriter.println(gson.toJson(errorReply));
                        } else {
                            final int scoreAdjustment = configuredQuestion.getSkipScore().intValue();
                            final boolean completed = SessionFactory.updateSessionAndAdvance(session, scoreAdjustment);
                            // todo add to history
                            final Reply reply = new Reply(completed, "Skipped.", scoreAdjustment);
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
        private String message;
        @SerializedName("score-adjustment")
        private int scoreAdjustment;

        public Reply(boolean completed, String message, int scoreAdjustment) {
            this.completed = completed;
            this.message = message;
            this.scoreAdjustment = scoreAdjustment;
        }
    }
}