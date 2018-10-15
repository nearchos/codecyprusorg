package org.codecyprus.th.api.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.model.Replies;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;

import static org.codecyprus.th.api.StartServlet.PARAMETER_APP;
import static org.codecyprus.th.api.StartServlet.PARAMETER_PLAYER;
import static org.codecyprus.th.api.StartServlet.PARAMETER_TREASURE_HUNT_ID;
import static org.codecyprus.th.api.test.TestStartServlet.StartError.*;

public class TestStartServlet extends HttpServlet {

    public static final String PARAMETER_ERROR = "player";
    public enum StartError { INACTIVE, EMPTY, PLAYER, APP, UNKNOWN, MISSING_PARAMETER, DEFAULT};
    public static final Map<StartError,String> ERROR_MESSAGES = new HashMap<>();
    static {
        ERROR_MESSAGES.put(INACTIVE, "The specified treasure hunt is not active right now.");
        ERROR_MESSAGES.put(EMPTY, "The specified treasure hunt is empty (i.e. contains no questions).");
        ERROR_MESSAGES.put(PLAYER, "The specified playerName: Homer, is already in use (try a different one).");
        ERROR_MESSAGES.put(APP, "Missing or empty parameter: " + PARAMETER_APP);
        ERROR_MESSAGES.put(UNKNOWN, "Could not find a treasure hunt for the specified id: 123");
        ERROR_MESSAGES.put(MISSING_PARAMETER, "Multiple error messages of the form 'Missing or empty parameter: ...'");
        ERROR_MESSAGES.put(DEFAULT, "Same as with '" + MISSING_PARAMETER + "'");
    }

    public static final Logger log = Logger.getLogger("codecyprus-test-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final String error = request.getParameter(PARAMETER_ERROR);
        if(error == null || error.isEmpty()) {
            final Replies.StartReply reply = new Replies.StartReply(UUID.randomUUID().toString(), getRandomNumOfQuestions());
            printWriter.println(gson.toJson(reply));
        } else { // handle error messages
            // parse to JSON and return errors
            StartError startError;
            try {
                startError = StartError.valueOf(error.toUpperCase().trim());
            } catch (IllegalArgumentException iae) {
                startError = StartError.DEFAULT;
            }
            switch (startError) {
                case INACTIVE: {
                    // parse to JSON and return errors
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply(ERROR_MESSAGES.get(INACTIVE));
                    printWriter.println(gson.toJson(errorReply));
                    break;
                }
                case EMPTY: {
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply(ERROR_MESSAGES.get(EMPTY));
                    printWriter.println(gson.toJson(errorReply));
                    break;
                }
                case PLAYER: {
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply(ERROR_MESSAGES.get(PLAYER));
                    printWriter.println(gson.toJson(errorReply));
                    break;
                }
                case APP: {
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply(ERROR_MESSAGES.get(APP));
                    printWriter.println(gson.toJson(errorReply));
                    break;
                }
                case UNKNOWN: {
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply(ERROR_MESSAGES.get(UNKNOWN));
                    printWriter.println(gson.toJson(errorReply));
                    break;
                }
                case MISSING_PARAMETER:
                case DEFAULT:
                default: {
                    final ArrayList<String> errorMessages = new ArrayList<>();
                    errorMessages.add("Missing or empty parameter: " + PARAMETER_PLAYER);
                    errorMessages.add("Missing or empty parameter: " + PARAMETER_APP);
                    errorMessages.add("Missing or empty parameter: " + PARAMETER_TREASURE_HUNT_ID );
                    // parse to JSON and return errors
                    final Replies.ErrorReply errorReply = new Replies.ErrorReply(errorMessages);
                    printWriter.println(gson.toJson(errorReply));
                    break;
                }
            }
        }
    }

    private static Random random = new Random();

    private int getRandomNumOfQuestions() {
        final int minNumOfQuestions = 5;
        final int maxNumOfQuestions = 15;
        return minNumOfQuestions + random.nextInt(maxNumOfQuestions - minNumOfQuestions + 1);
    }
}