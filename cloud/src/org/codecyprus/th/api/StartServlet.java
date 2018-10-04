package org.codecyprus.th.api;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.db.ConfiguredQuestionFactory;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.db.TreasureHuntFactory;
import org.codecyprus.th.model.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class StartServlet extends HttpServlet {

    public static final String PARAMETER_PLAYER = "player";
    public static final String PARAMETER_APP = "app";
    public static final String PARAMETER_TREASURE_HUNT_ID = "treasure-hunt-id";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Handles request to start a new treasure hunt {@link org.codecyprus.th.model.Session}.
     * URL form: /api/start?player=...&app=...&treasure-hunt-id=...
     *
     * Example:
     * <ul>
     *     <li><b>/api/start?player=Nearchos&app=android-app&treasure-hunt-id=agl1Y2...</b> starts a new
     *     {@link org.codecyprus.th.model.Session}, where the player name, and the app are as specified, and the
     *     selected {@link org.codecyprus.th.model.TreasureHunt} is determined by its id.</li>
     * </ul>
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final ArrayList<String> errorMessages = new ArrayList<>();

        // get parameters
        final String playerName = request.getParameter(PARAMETER_PLAYER);
        final String app = request.getParameter(PARAMETER_APP);
        final String treasureHuntId = request.getParameter(PARAMETER_TREASURE_HUNT_ID);

        // check for errors/missing parameters
        if(playerName == null || playerName.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_PLAYER);
        }
        if(app == null || app.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_APP);
        }
        if(treasureHuntId == null || treasureHuntId.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_TREASURE_HUNT_ID );
        }

        if(!errorMessages.isEmpty()) {
            // parse to JSON and return errors
            final Replies.ErrorReply errorReply = new Replies.ErrorReply(errorMessages);
            printWriter.println(gson.toJson(errorReply));
        } else {
            // first retrieve corresponding treasure hunt...
            final TreasureHunt treasureHunt = TreasureHuntFactory.getTreasureHunt(treasureHuntId);
            if(treasureHunt == null) {
                // parse to JSON and return errors
                final Replies.ErrorReply errorReply = new Replies.ErrorReply("Could not find a treasure hunt for the specified id: " + treasureHuntId);
                printWriter.println(gson.toJson(errorReply));
            } else {
                if(!treasureHunt.isActiveNow()) {
                    // parse to JSON and return errors
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply("The specified treasure hunt is not active right now.");
                    printWriter.println(gson.toJson(errorReply));
                } else {
//                    if(treasureHunt.isRequiresAuthentication()) { /* todo handle it */ }

                    // ...next retrieve treasure hunt's questions
                    final ArrayList<ConfiguredQuestion> configuredQuestions = ConfiguredQuestionFactory.getConfiguredQuestionsForTreasureHunt(treasureHuntId);
                    if(configuredQuestions.isEmpty()) {
                        // parse to JSON and return errors
                        final Replies.ErrorReply errorReply = new Replies.ErrorReply("The specified treasure hunt is empty (i.e. contains no questions).");
                        printWriter.println(gson.toJson(errorReply));
                    } else {
                        // prepare and create session
                        final long startTime = System.currentTimeMillis();
                        final int score = 0;
                        final ArrayList<String> configuredQuestionsList = getIds(configuredQuestions);
                        if(treasureHunt.isShuffled()) {
                            Collections.shuffle(configuredQuestionsList);
                        }

                        final long endTime = treasureHunt.getMaxDuration() > 0 ? // non-zero means each player gets a fixed time
                            startTime + treasureHunt.getMaxDuration() : // endTime is maxDuration after start
                            treasureHunt.getEndsOn(); // endTime is treasureHunt end time

                        final Session session = new Session(treasureHuntId, playerName, app, startTime, endTime, configuredQuestionsList);
                        final Key key = SessionFactory.synchronouslyAddSession(session);

                        if(key != null) {
                            final String sessionId = KeyFactory.keyToString(key);
                            // parse to JSON and return results
                            final Replies.StartReply reply = new Replies.StartReply(sessionId, configuredQuestionsList.size());
                            printWriter.println(gson.toJson(reply));
                        } else { // specified playerName already exists for given treasure hunt
                            // parse to JSON and return errors
                            final Replies.ErrorReply errorReply = new Replies.ErrorReply("The specified playerName: " + playerName + ", is already in use (try a different one).");
                            printWriter.println(gson.toJson(errorReply));
                        }
                    }
                }
            }
        }
    }

    private ArrayList<String> getIds(final ArrayList<ConfiguredQuestion> configuredQuestions) {
        final ArrayList<String> configuredQuestionIds = new ArrayList<>();
        for(final ConfiguredQuestion configuredQuestion : configuredQuestions) {
            configuredQuestionIds.add(configuredQuestion.getUuid());
        }
        return configuredQuestionIds;
    }
}