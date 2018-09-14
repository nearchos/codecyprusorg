package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.model.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Logger;

public class LeaderboardServlet extends HttpServlet {

    public static final int MIN_LIMIT = 5;

    public static final String PARAMETER_SESSION = "session";
    public static final String PARAMETER_TREASURE_HUNT_ID = "treasure-hunt-id";
    public static final String PARAMETER_SORTED = "sorted";
    public static final String PARAMETER_LIMIT = "limit";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final String sessionId = request.getParameter(PARAMETER_SESSION);
        final String treasureHuntId = request.getParameter(PARAMETER_TREASURE_HUNT_ID);
        final boolean sorted = request.getParameter(PARAMETER_SORTED) != null;
        final String limitS = request.getParameter(PARAMETER_LIMIT);
        int limit = Integer.MAX_VALUE;
        if(sorted) { // try to set a limit only if the sorted flag was set
            try {
                limit = Integer.parseInt(limitS);
                if(limit < MIN_LIMIT) limit = MIN_LIMIT; // limit cannot be less than a positive threshold (min value)
            } catch (NumberFormatException nfe) {
                // silently ignore
            }
        }

        final boolean sessionIdSpecified = sessionId != null && !sessionId.trim().isEmpty();
        final boolean treasureHuntIdSpecified = treasureHuntId != null && !treasureHuntId.trim().isEmpty();

        // check for errors/missing parameters
        if(!sessionIdSpecified && !treasureHuntIdSpecified) {
            final ErrorReply errorReply = new ErrorReply("Missing or empty parameters. Must define exactly one of these parameters: " + PARAMETER_SESSION + ", " + PARAMETER_TREASURE_HUNT_ID);
            printWriter.println(gson.toJson(errorReply));
        } else if(sessionIdSpecified && treasureHuntIdSpecified) {
            final ErrorReply errorReply = new ErrorReply("Too many parameters. Must define exactly one of these parameters: " + PARAMETER_SESSION + ", " + PARAMETER_TREASURE_HUNT_ID);
            printWriter.println(gson.toJson(errorReply));
        } else {
            if(sessionIdSpecified) { // user-specific leaderboard
                final Session session = SessionFactory.getSession(sessionId);
                if(session == null) {
                    final ErrorReply errorReply = new ErrorReply("Unknown session. The specified session ID could not be found.");
                    printWriter.println(gson.toJson(errorReply));
                } else {
                    final Vector<Session> sessions = SessionFactory.getSessionsByTreasureHuntId(session.getTreasureHuntUuid());
                    if(sessions.isEmpty()) {
                        final ErrorReply errorReply = new ErrorReply("No Sessions for Session with laptoTreasure Hunt with id: " + session.getTreasureHuntUuid());
                        printWriter.println(gson.toJson(errorReply));
                    } else {
                        final Reply reply = new Reply(sorted, limit, sessions);
                        printWriter.println(gson.toJson(reply));
                    }
                }
            } else { // general, treasure hunt leaderboard
                final Vector<Session> sessions = SessionFactory.getSessionsByTreasureHuntId(treasureHuntId);
                if(sessions.isEmpty()) {
                    final ErrorReply errorReply = new ErrorReply("No Sessions for Treasure Hunt with id: " + treasureHuntId);
                    printWriter.println(gson.toJson(errorReply));
                } else {
                    final Reply reply = new Reply(sorted, limit, sessions);
                    printWriter.println(gson.toJson(reply));
                }
            }
        }
    }

    public class Reply implements Serializable {

        private String status = "OK";
        @SerializedName("numOfPlayers")
        private int numOfPlayers;
        private boolean sorted;
        private int limit;
        private Vector<LeaderboardEntry> leaderboard;

        Reply(final boolean sorted, final int limit, final Vector<Session> sessions) {
            this.numOfPlayers = sessions.size();
            this.sorted = sorted;
            this.limit = limit;
            this.leaderboard = new Vector<>();
            // add all entries
            for(final Session session : sessions) {
                this.leaderboard.add(new LeaderboardEntry(session.getPlayerName(), session.getScore(), session.getCompletionTime()));
            }

            if(sorted) { // sort if needed
                Collections.sort(leaderboard);
                while(leaderboard.size() > limit) { // remove last item until leaderboard has <= limit
                    leaderboard.remove(leaderboard.size() - 1);
                }
            } else { // shuffle
                Collections.shuffle(leaderboard);
            }
        }
    }

    public class LeaderboardEntry implements Comparable<LeaderboardEntry>, Serializable {
        private String player;
        private long score;
        @SerializedName("completionTime")
        private long completionTime;

        public LeaderboardEntry(String player, long score, long completionTime) {
            this.player = player;
            this.score = score;
            this.completionTime = completionTime;
        }

        @Override
        public int compareTo(LeaderboardEntry other) {
            // first compare by score (higher is 'before')
            final int scoreCompare = Long.compare(this.score, other.score);
            if(scoreCompare != 0) {
                return -scoreCompare;
            } else {
                // if scores are equal, compare completion times (smaller is 'before' except 0 which means unfinished which is the largest)
                return Long.compare(this.completionTime == 0 ? Integer.MAX_VALUE : this.completionTime,
                        other.completionTime == 0 ? Integer.MAX_VALUE : other.completionTime);
            }
        }

        @Override
        public String toString() {
            return "\nLeaderboardEntry{" +
                    "player='" + player + '\'' +
                    ", score=" + score +
                    ", completionTime=" + completionTime +
                    '}';
        }
    }
}