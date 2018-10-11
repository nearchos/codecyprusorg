package org.codecyprus.th.api.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.api.Common;
import org.codecyprus.th.model.Replies;
import org.codecyprus.th.model.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

public class TestLeaderboardServlet extends HttpServlet {

    public static final String PARAMETER_SIZE = "size";
    public static final String PARAMETER_SORTED = "sorted";

    public static final int TEN_MINUTES = 10 * 60 * 1000;
    public static final int DEFAULT_SIZE = 42;
    public static final int MAX_SCORE = 100;

    public static final Logger log = Logger.getLogger("codecyprus-test-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Random random = new Random();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final String sizeS = request.getParameter(PARAMETER_SIZE);
        int size;
        try {
            size = sizeS == null ? DEFAULT_SIZE : Integer.parseInt(sizeS);
        } catch (NumberFormatException nfe) {
            size = DEFAULT_SIZE;
        }
        if(size < 0 || size > 1000) size = DEFAULT_SIZE;
        final boolean sorted = Common.checkUrlBooleanParameter(request.getParameter(PARAMETER_SORTED));

        int limit = Integer.MAX_VALUE;
        final String treasureHuntName = "Test Treasure Hunt";
        final Vector<Session> sessions = getSessions(size);
        final Replies.LeaderboardReply reply = new Replies.LeaderboardReply(sorted, limit, sessions, treasureHuntName);
        printWriter.println(gson.toJson(reply));
    }

    private Vector<Session> getSessions(final int size) {
        final Vector<Session> sessions = new Vector<>();

        final long commonStartTime = System.currentTimeMillis() - TEN_MINUTES;
        final String treasureHunUUID = UUID.randomUUID().toString();
        for(int i = 0; i < size; i++) {
            final int completionTime = flipCoin() ? 0 : 1 + random.nextInt(TEN_MINUTES); // choose randomly between 0 (unfinished) and a finishing time from 0 to 10 minutes
            sessions.add(new Session(
                    UUID.randomUUID().toString(),
                    treasureHunUUID,
                    "player-" + i,
                    "app-" + i,
                    commonStartTime,
                    commonStartTime + TEN_MINUTES / 2 - random.nextInt(TEN_MINUTES / 2), // end time is random around start time plus 5 mins
                    random.nextInt(MAX_SCORE),
                    completionTime,
                    null,
                    null
                    ));
        }
        return sessions;
    }

    private boolean flipCoin() {
        return random.nextBoolean();
    }
}