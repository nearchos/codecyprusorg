package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.db.LocationFactory;
import org.codecyprus.th.db.SessionFactory;
import org.codecyprus.th.model.Location;
import org.codecyprus.th.model.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

public class LocationServlet extends HttpServlet {

    public static final long MINIMUM_DELAY_BETWEEN_UPDATES = 30L * 1000; // 30 seconds

    public static final String PARAMETER_SESSION = "session";
    public static final String PARAMETER_LATITUDE = "latitude";
    public static final String PARAMETER_LONGITUDE = "longitude";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        final ArrayList<String> errorMessages = new ArrayList<>();

        final String sessionId = request.getParameter(PARAMETER_SESSION);
        final String latitudeS = request.getParameter(PARAMETER_LATITUDE);
        final String longitudeS = request.getParameter(PARAMETER_LONGITUDE);

        // check for errors/missing parameters
        if(sessionId == null || sessionId.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_SESSION);
        }
        if(latitudeS == null || latitudeS.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_LATITUDE);
        }
        if(longitudeS == null || longitudeS.trim().isEmpty()) {
            errorMessages.add("Missing or empty parameter: " + PARAMETER_LONGITUDE);
        }
        double latitude = 0d;
        try {
            latitude = latitudeS == null ? 0d : Double.parseDouble(latitudeS);
        } catch (NumberFormatException nfe) {
            errorMessages.add("Invalid non-numeric parameter: " + PARAMETER_LATITUDE);
        }
        double longitude = 0d;
        try {
            longitude = longitudeS == null ? 0d : Double.parseDouble(longitudeS);
        } catch (NumberFormatException nfe) {
            errorMessages.add("Invalid non-numeric parameter: " + PARAMETER_LONGITUDE);
        }

        if(!errorMessages.isEmpty()) {
            final ErrorReply errorReply = new ErrorReply(errorMessages);
            printWriter.println(gson.toJson(errorReply));
        } else {
            final Session session = SessionFactory.getSession(sessionId);
            if(session == null) {
                final ErrorReply errorReply = new ErrorReply("Unknown session. The specified session ID could not be found.");
                printWriter.println(gson.toJson(errorReply));
            } else {
                if(session.isCompleted()) {
                    final ErrorReply errorReply = new ErrorReply("Completed session. The specified session has no more unanswered questions.");
                    printWriter.println(gson.toJson(errorReply));
                } else if(session.isFinished()) {
                    final ErrorReply errorReply = new ErrorReply("Finished session. The specified session has run out of time.");
                    printWriter.println(gson.toJson(errorReply));
                } else {
                    final Location location = LocationFactory.getLatestLocation(sessionId);
                    final long now = System.currentTimeMillis();
                    if(location != null && location.getTimestamp() + MINIMUM_DELAY_BETWEEN_UPDATES > now) {
                        final Reply reply = new Reply("Ignored update as the previous update was less than " + MINIMUM_DELAY_BETWEEN_UPDATES/1000 + " seconds earlier.");
                        printWriter.println(gson.toJson(reply));
                    } else {
                        LocationFactory.addLocation(new Location(sessionId, now, latitude, longitude));
                        final Reply reply = new Reply("Added location (" + latitude + ", " + longitude + ")");
                        printWriter.println(gson.toJson(reply));
                    }
                }
            }
        }
    }

    public class Reply implements Serializable {

        private String status = "OK";
        private String message;

        public Reply(String message) {
            this.message = message;
        }
    }
}