package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.db.LocationFactory;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.db.TreasureHuntFactory;
import org.codecyprus.th.model.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

public class LeaderboardWithLocationsServlet extends HttpServlet {

    public static final String PARAMETER_TREASURE_HUNT_ID = "treasure-hunt-id";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain; charset=utf-8");
//        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final String treasureHuntId = request.getParameter(PARAMETER_TREASURE_HUNT_ID);

        final TreasureHunt treasureHunt = TreasureHuntFactory.getTreasureHunt(treasureHuntId);
        assert treasureHunt != null;
        final Vector<Session> sessions = SessionFactory.getSessionsByTreasureHuntId(treasureHuntId);
        final Map<String, Coordinates> latestCoordinatesMap = new HashMap<>();
        for(final Session session : sessions) {
            final String sessionUuid = session.getUuid();
            final Location location = LocationFactory.getLatestLocation(sessionUuid);
            if(location != null) {
                latestCoordinatesMap.put(sessionUuid, new Coordinates(location.getLatitude(), location.getLongitude()));
            }
        }
        final boolean hasPrize = treasureHunt.isHasPrize();
        final Replies.LeaderboardWithLocationReply reply = new Replies.LeaderboardWithLocationReply(
                true, Integer.MAX_VALUE, hasPrize, treasureHunt.getStartsOn(), treasureHunt.getEndsOn(), sessions, treasureHunt.getName(), latestCoordinatesMap);
        printWriter.println(gson.toJson(reply));
    }
}
