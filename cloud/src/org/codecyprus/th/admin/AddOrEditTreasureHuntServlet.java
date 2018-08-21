package org.codecyprus.th.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.codecyprus.th.db.TreasureHuntFactory;
import org.codecyprus.th.db.UserFactory;
import org.codecyprus.th.model.TreasureHunt;
import org.codecyprus.th.model.User;
import org.codecyprus.th.model.Visibility;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;

public class AddOrEditTreasureHuntServlet extends HttpServlet {

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
                String uuid = request.getParameter(TreasureHuntFactory.PROPERTY_UUID);
                final String name = request.getParameter(TreasureHuntFactory.PROPERTY_NAME);
                final String description = request.getParameter(TreasureHuntFactory.PROPERTY_DESCRIPTION);
                final String ownerEmail = user.getEmail();
                final String visibilityS = request.getParameter(TreasureHuntFactory.PROPERTY_VISIBILITY);
                Visibility visibility = Visibility.UNLISTED;
                try {
                    visibility = Visibility.valueOf(visibilityS);
                } catch (IllegalArgumentException iae) {
                    log.warning("Could not parse visibility value: " + visibilityS);
                }
                long startsOn;
                long endsOn;
                long maxDuration;
                try {
                    startsOn = TreasureHuntFactory.SIMPLE_DATE_FORMAT.parse(request.getParameter(TreasureHuntFactory.PROPERTY_STARTS_ON)).getTime();
                } catch (ParseException pe) {
                    startsOn = 0L;
                    log.warning(pe.getMessage());
                }
                try {
                    endsOn = TreasureHuntFactory.SIMPLE_DATE_FORMAT.parse(request.getParameter(TreasureHuntFactory.PROPERTY_ENDS_ON)).getTime();
                } catch (ParseException pe) {
                    endsOn = 0L;
                    log.warning(pe.getMessage());
                }
                try {
                    maxDuration = Integer.parseInt(request.getParameter(TreasureHuntFactory.PROPERTY_MAX_DURATION));
                } catch (NumberFormatException nfe) {
                    maxDuration = 0L;
                    log.warning(nfe.getMessage());
                }
                final boolean shuffled = "on".equalsIgnoreCase(request.getParameter(TreasureHuntFactory.PROPERTY_SHUFFLED));
                final boolean requiresAuthentication = "on".equalsIgnoreCase(request.getParameter(TreasureHuntFactory.PROPERTY_REQUIRES_AUTHENTICATION));
                final boolean emailResults = "on".equalsIgnoreCase(request.getParameter(TreasureHuntFactory.PROPERTY_EMAIL_RESULTS));

                if(uuid != null && !uuid.isEmpty()) { // editing existing category
                    final TreasureHunt treasureHunt = new TreasureHunt(uuid, name, description, ownerEmail, visibility, startsOn, endsOn, maxDuration, shuffled, requiresAuthentication, emailResults);
                    TreasureHuntFactory.editTreasureHunt(treasureHunt);

                    // use ably to update treasure hunt name
                    pushAblyUpdate(uuid, name, startsOn, endsOn);
                } else { // adding a new category
                    final TreasureHunt treasureHunt = new TreasureHunt(name, description, ownerEmail, visibility, startsOn, endsOn, maxDuration, shuffled, requiresAuthentication, emailResults);
                    final Key key = TreasureHuntFactory.addTreasureHunt(treasureHunt);
                    uuid = KeyFactory.keyToString(key);
                }

                response.sendRedirect("treasure-hunt?uuid=" + uuid);
            }
        }
    }

    private void pushAblyUpdate(final String uuid, final String name, final long startsOn, final long endsOn) {
        // todo integrate ably
    }
}