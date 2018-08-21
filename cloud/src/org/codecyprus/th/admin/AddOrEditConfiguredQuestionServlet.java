package org.codecyprus.th.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.codecyprus.th.db.ConfiguredQuestionFactory;
import org.codecyprus.th.db.UserFactory;
import org.codecyprus.th.model.ConfiguredQuestion;
import org.codecyprus.th.model.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class AddOrEditConfiguredQuestionServlet extends HttpServlet {

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final UserService userService = UserServiceFactory.getUserService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");

        if(userService.getCurrentUser() == null) {
            response.getWriter().print("You must sign in first");
        } else {
            final String email = userService.getCurrentUser().getEmail();
            final User user = UserFactory.getUserByEmail(email);
            final boolean isAdmin = user != null && user.isAdmin();
            if(user == null || !isAdmin) {
                response.getWriter().print("User is not an admin: " + email);
            } else {
                String uuid = request.getParameter(ConfiguredQuestionFactory.PROPERTY_UUID);
                final String treasureHuntId = request.getParameter(ConfiguredQuestionFactory.PROPERTY_TREASURE_HUNT_ID);
                final String questionId = request.getParameter(ConfiguredQuestionFactory.PROPERTY_QUESTION_ID);
                final String seqNumberS = request.getParameter(ConfiguredQuestionFactory.PROPERTY_SEQ_NUMBER);
                long seqNumber = 0;
                try {
                    seqNumber = Integer.parseInt(seqNumberS);
                } catch (NumberFormatException nfe) {
                    log.warning("Could not parse 'seqNumber' value: " + seqNumberS);
                }
                final String correctScoreS = request.getParameter(ConfiguredQuestionFactory.PROPERTY_CORRECT_SCORE);
                long correctScore = Defaults.DEFAULT_CORRECT_SCORE;
                try {
                    correctScore = Integer.parseInt(correctScoreS);
                } catch (NumberFormatException nfe) {
                    log.warning("Could not parse 'correctScore' value: " + correctScoreS);
                }
                final String wrongScoreS = request.getParameter(ConfiguredQuestionFactory.PROPERTY_WRONG_SCORE);
                long wrongScore = Defaults.DEFAULT_WRONG_SCORE;
                try {
                    wrongScore = Integer.parseInt(wrongScoreS);
                } catch (NumberFormatException nfe) {
                    log.warning("Could not parse 'wrongScore' value: " + wrongScoreS);
                }
                final String skipScoreS = request.getParameter(ConfiguredQuestionFactory.PROPERTY_SKIP_SCORE);
                long skipScore = Defaults.DEFAULT_SKIP_SCORE;
                try {
                    skipScore = Integer.parseInt(skipScoreS);
                } catch (NumberFormatException nfe) {
                    log.warning("Could not parse 'skipScore' value: " + skipScoreS);
                }
                final boolean canBeSkipped = "on".equalsIgnoreCase(request.getParameter(ConfiguredQuestionFactory.PROPERTY_CAN_BE_SKIPPED));
                final String latitudeS = request.getParameter(ConfiguredQuestionFactory.PROPERTY_LATITUDE);
                double latitude = 0d;
                try {
                    latitude = Double.parseDouble(latitudeS);
                } catch (NumberFormatException nfe) {
                    log.warning("Could not parse 'latitude' value: " + latitudeS);
                }
                final String longitudeS = request.getParameter(ConfiguredQuestionFactory.PROPERTY_LONGITUDE);
                double longitude = 0d;
                try {
                    longitude = Double.parseDouble(longitudeS);
                } catch (NumberFormatException nfe) {
                    log.warning("Could not parse 'longitude' value: " + longitudeS);
                }
                final String distanceThresholdS = request.getParameter(ConfiguredQuestionFactory.PROPERTY_DISTANCE_THRESHOLD);
                double distanceThreshold = 0d;
                try {
                    distanceThreshold = Double.parseDouble(distanceThresholdS);
                } catch (NumberFormatException nfe) {
                    log.warning("Could not parse 'distanceThreshold' value: " + distanceThresholdS);
                }

                if(uuid != null && !uuid.isEmpty()) { // editing existing configured question
                    final ConfiguredQuestion configuredQuestion = new ConfiguredQuestion(uuid, treasureHuntId, questionId, seqNumber, correctScore, wrongScore, skipScore, canBeSkipped, latitude, longitude, distanceThreshold);
                    ConfiguredQuestionFactory.editConfiguredQuestion(configuredQuestion);
                } else { // adding a new category
                    final ConfiguredQuestion configuredQuestion = new ConfiguredQuestion(treasureHuntId, questionId, seqNumber, correctScore, wrongScore, skipScore, canBeSkipped, latitude, longitude, distanceThreshold);
                    final Key key = ConfiguredQuestionFactory.addConfiguredQuestion(configuredQuestion);
                    uuid = KeyFactory.keyToString(key);
                    log.info("Added new ConfiguredQuestion with UUID: " + uuid);
                }

                final String redirectUrl = request.getParameter("redirect");
                response.sendRedirect(redirectUrl);
            }
        }
    }
}
