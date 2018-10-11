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

public class TestAnswerServlet extends HttpServlet {

    public static final String PARAMETER_CORRECT = "correct";
    public static final String PARAMETER_COMPLETED = "completed";

    public static final Logger log = Logger.getLogger("codecyprus-test-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final boolean correct = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_CORRECT));
        final boolean completed = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_COMPLETED));

        final String message = correct ? "Correct answer, well done!" : "Wrong answer, try again.";
        final int scoreAdjustment = correct ? 10: -3;

        final Replies.AnswerReply reply = new Replies.AnswerReply(correct, completed, message, scoreAdjustment);
        printWriter.println(gson.toJson(reply));
    }
}
