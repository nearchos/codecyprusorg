package org.codecyprus.th.api.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.api.Common;
import org.codecyprus.th.model.Replies;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class TestScoreServlet extends HttpServlet {

    public static final String PARAMETER_SCORE = "score";
    public static final String PARAMETER_COMPLETED = "completed";
    public static final String PARAMETER_FINISHED = "finished";
    public static final String PARAMETER_ERROR = "error";

    public static final int DEFAULT_SCORE = 42;
    public static final int MIN_SCORE = -1000;
    public static final int MAX_SCORE = +1000;

    public static final Logger log = Logger.getLogger("codecyprus-test-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final String scoreS = request.getParameter(PARAMETER_SCORE);
        int score;
        try {
            score = scoreS == null ? DEFAULT_SCORE : Integer.parseInt(scoreS);
        } catch (NumberFormatException nfe) {
            score = DEFAULT_SCORE;
        }
        if(score < MIN_SCORE) score = MIN_SCORE;
        if(score > MAX_SCORE) score = MAX_SCORE;
        final boolean completed = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_COMPLETED));
        final boolean finished = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_FINISHED));
        final boolean error = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_ERROR));

        if(error) {
            final Replies.ErrorReply errorReply = new Replies.ErrorReply("Unknown session. The specified session ID could not be found.");
            printWriter.println(gson.toJson(errorReply));
        } else {
            final String player = "PlayerUnknown";
            final Replies.ScoreReply reply = new Replies.ScoreReply(completed, finished, player, score, false);
            printWriter.println(gson.toJson(reply));
        }
    }
}