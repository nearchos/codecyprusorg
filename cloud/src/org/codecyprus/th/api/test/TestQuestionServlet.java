package org.codecyprus.th.api.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.api.Common;
import org.codecyprus.th.model.QuestionType;
import org.codecyprus.th.model.Replies;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Logger;

public class TestQuestionServlet extends HttpServlet {

    public static final String PARAMETER_COMPLETED = "completed";
    public static final String PARAMETER_QUESTION_TYPE = "question-type";
    public static final String PARAMETER_CAN_BE_SKIPPED = "can-be-skipped";
    public static final String PARAMETER_REQUIRES_LOCATION  = "requires-location";

    public static final Logger log = Logger.getLogger("codecyprus-test-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Random random = new Random();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final boolean completed = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_COMPLETED));
        final String questionTypeS = request.getParameter(PARAMETER_QUESTION_TYPE);
        QuestionType questionType;
        try {
            questionType = questionTypeS == null ? QuestionType.TEXT : QuestionType.valueOf(questionTypeS.toUpperCase().trim());
        } catch (IllegalArgumentException iae) {
            questionType = QuestionType.TEXT; // default value
        }
        final boolean canBeSkipped = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_CAN_BE_SKIPPED));
        final boolean requiresLocation = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_REQUIRES_LOCATION));

        final String questionText = "This is a sample question. As such it also contains some <code>HTML</code> and even a <a href=http://codcyprus.org>link<a/>.";
        final int randomNumOfQuestions = 10 + random.nextInt(10); // 10 to 19
        final int randomIndex = random.nextInt(randomNumOfQuestions); // 0 to randomNumOfQuestions-1
        final long correctScore = 10, wrongScore = -3, skipScore = -5; // init to some reasonable numbers

        final Replies.QuestionReply reply = new Replies.QuestionReply(completed, questionText, questionType, canBeSkipped, requiresLocation, randomNumOfQuestions, randomIndex, correctScore, wrongScore, skipScore);
        printWriter.println(gson.toJson(reply));
    }
}
