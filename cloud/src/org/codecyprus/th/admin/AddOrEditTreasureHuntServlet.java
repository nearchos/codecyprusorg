package org.codecyprus.th.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import io.ably.lib.rest.AblyRest;
import io.ably.lib.rest.Channel;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.Message;
import org.codecyprus.th.db.ParameterFactory;
import org.codecyprus.th.db.TreasureHuntFactory;
import org.codecyprus.th.db.UserFactory;
import org.codecyprus.th.model.Parameter;
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
                final String secretCode = request.getParameter(TreasureHuntFactory.PROPERTY_SECRET_CODE);
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
                    startsOn = TreasureHunt.SIMPLE_DATE_FORMAT.parse(request.getParameter(TreasureHuntFactory.PROPERTY_STARTS_ON)).getTime();
                } catch (ParseException pe) {
                    startsOn = 0L;
                    log.warning(pe.getMessage());
                }
                try {
                    endsOn = TreasureHunt.SIMPLE_DATE_FORMAT.parse(request.getParameter(TreasureHuntFactory.PROPERTY_ENDS_ON)).getTime();
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
                final boolean hasPrize = "on".equalsIgnoreCase(request.getParameter(TreasureHuntFactory.PROPERTY_HAS_PRIZE));

                if(uuid != null && !uuid.isEmpty()) { // editing existing category
                    final TreasureHunt treasureHunt = new TreasureHunt(uuid, name, description, ownerEmail, secretCode, visibility, startsOn, endsOn, maxDuration, shuffled, requiresAuthentication, emailResults, hasPrize);
                    TreasureHuntFactory.editTreasureHunt(treasureHunt);
                    // use ably to update treasure hunt name
                    pushAblyUpdate(uuid, name, startsOn, endsOn);
                } else { // adding a new category
                    final TreasureHunt treasureHunt = new TreasureHunt(name, description, ownerEmail, secretCode, visibility, startsOn, endsOn, maxDuration, shuffled, requiresAuthentication, emailResults, hasPrize);
                    final Key key = TreasureHuntFactory.addTreasureHunt(treasureHunt);
                    uuid = KeyFactory.keyToString(key);
                }

                response.sendRedirect("treasure-hunt?uuid=" + uuid);
            }
        }
    }

    private static final String EOL = System.getProperty("line.separator");

    private void pushAblyUpdate(final String uuid, final String name, final long startsOn, final long endsOn) {
        try {
            // ably push
            final Parameter parameter = ParameterFactory.getParameter("ABLY_PRIVATE_KEY");
            if(parameter != null) {
                final String ablyKey = parameter.getValue();
                final AblyRest ably = new AblyRest(ablyKey);
                final Channel channel = ably.channels.get("th-" + uuid);
                final String json = "  {" + EOL +
                        "    \"name\": \"" + name + "\"," + EOL +
                        "    \"startsOn\": " + startsOn + "," + EOL +
                        "    \"endsOn\": " + endsOn + EOL +
                        "  }" + EOL;
                final Message[] messages = new Message[] {new Message("th_update", json)};
                channel.publish(messages);
            }
        } catch (AblyException ae) {
            log.severe("Ably problem: " + ae.errorInfo.message);
        }
    }
}