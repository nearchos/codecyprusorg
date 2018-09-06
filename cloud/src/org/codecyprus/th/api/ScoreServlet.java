package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.model.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.logging.Logger;

public class ScoreServlet extends HttpServlet {

    public static final String PARAMETER_SESSION = "session";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final String sessionId = request.getParameter(PARAMETER_SESSION);

        // check for errors/missing parameters
        if(sessionId == null || sessionId.trim().isEmpty()) {
            final ErrorReply errorReply = new ErrorReply("Missing or empty parameter: " + PARAMETER_SESSION);
            printWriter.println(gson.toJson(errorReply));
        } else {
            final Session session = SessionFactory.getSession(sessionId);
            if(session == null) {
                final ErrorReply errorReply = new ErrorReply("Unknown session. The specified session ID could not be found.");
                printWriter.println(gson.toJson(errorReply));
            } else {
                final Reply reply = new Reply(session.isCompleted(), session.isFinished(), session.getScore());
                printWriter.println(gson.toJson(reply));
            }
        }
    }

    public class Reply implements Serializable {

        private String status = "OK";
        private boolean completed;
        private boolean finished;
        private long score;

        public Reply(boolean completed, boolean finished, long score) {
            this.completed = completed;
            this.finished = finished;
            this.score = score;
        }
    }
}