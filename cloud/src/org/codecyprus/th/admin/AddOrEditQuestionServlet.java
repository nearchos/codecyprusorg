package org.codecyprus.th.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.codecyprus.th.db.QuestionFactory;
import org.codecyprus.th.db.UserFactory;
import org.codecyprus.th.model.Question;
import org.codecyprus.th.model.QuestionType;
import org.codecyprus.th.model.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class AddOrEditQuestionServlet extends HttpServlet {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final UserService userService = UserServiceFactory.getUserService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");

        if(userService.getCurrentUser() == null) {
            response.getWriter().print("You must sign in first");
        } else {
            final String email = userService.getCurrentUser().getEmail();
            final User userEntity = UserFactory.getUserByEmail(email);
            final boolean isAdmin = userEntity != null && userEntity.isAdmin();
            if(userEntity == null || !isAdmin) {
                response.getWriter().print("User is not an admin: " + email);
            } else {
                String uuid = request.getParameter(QuestionFactory.PROPERTY_UUID);
                final String questionText = request.getParameter(QuestionFactory.PROPERTY_QUESTION_TEXT);
                final String questionTypeS = request.getParameter(QuestionFactory.PROPERTY_QUESTION_TYPE);
                QuestionType questionType = QuestionType.TEXT;
                try {
                    questionType = QuestionType.valueOf(questionTypeS);
                } catch (IllegalArgumentException iae) {
                    log.warning("Could not parse 'questionType' value: " + questionTypeS);
                }
                final String correctAnswer = request.getParameter(QuestionFactory.PROPERTY_CORRECT_ANSWER);
                final String creatorEmail = request.getParameter(QuestionFactory.PROPERTY_CREATOR_EMAIL);
                final String creationTimestampS = request.getParameter(QuestionFactory.PROPERTY_CREATION_TIMESTAMP);
                long creationTimestamp = System.currentTimeMillis();
                try {
                    creationTimestamp = Long.parseLong(creationTimestampS);
                } catch (NumberFormatException nfe) {
                    log.warning("Could not parse 'creationTimestamp' value: " + creationTimestampS);
                }
                final boolean shared = "on".equalsIgnoreCase(request.getParameter(QuestionFactory.PROPERTY_SHARED));

                if(uuid != null && !uuid.isEmpty()) { // editing existing question
                    final Question question = new Question(uuid, questionText, questionType, correctAnswer, creatorEmail, creationTimestamp, shared);
                    QuestionFactory.editQuestion(question);
                } else { // adding a new category
                    final Question question = new Question(questionText, questionType, correctAnswer, email, creationTimestamp, shared);
                    final Key key = QuestionFactory.addQuestion(question);
                    uuid = KeyFactory.keyToString(key);
                    log.info("Added new Question with UUID: " + uuid);
                }

                final String redirectUrl = request.getParameter("redirect");
                response.sendRedirect(redirectUrl);
            }
        }
    }
}