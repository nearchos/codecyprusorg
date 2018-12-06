package org.codecyprus.th.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.codecyprus.th.db.TimedFactory;
import org.codecyprus.th.db.UserFactory;
import org.codecyprus.th.model.Timed;
import org.codecyprus.th.model.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class AddOrEditTimedServlet extends HttpServlet {

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
                String uuid = request.getParameter(TimedFactory.PROPERTY_UUID);
                final String treasureHuntId = request.getParameter(TimedFactory.PROPERTY_TREASURE_HUNT_ID);
                final String titleText = request.getParameter(TimedFactory.PROPERTY_TITLE_TEXT);
                final String bodyText = request.getParameter(TimedFactory.PROPERTY_BODY_TEXT);

                if(uuid != null && !uuid.isEmpty()) { // editing existing timed
                    final Timed timed = new Timed(uuid, treasureHuntId, titleText, bodyText);
                    TimedFactory.editTimed(timed);
                } else { // adding a new timed
                    final Timed timed = new Timed(treasureHuntId, titleText, bodyText);
                    final Key key = TimedFactory.addTimed(timed);
                    uuid = KeyFactory.keyToString(key);
                    log.info("Added new ConfiguredQuestion with UUID: " + uuid);
                }

                final String redirectUrl = request.getParameter("redirect");
                response.sendRedirect(redirectUrl);
            }
        }
    }
}