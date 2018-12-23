package org.codecyprus.th.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.codecyprus.th.db.UrlShortenerFactory;
import org.codecyprus.th.db.UserFactory;
import org.codecyprus.th.model.UrlShortener;
import org.codecyprus.th.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class AddOrEditUrlShortenerServlet extends HttpServlet {

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
                String uuid = request.getParameter(UrlShortenerFactory.PROPERTY_UUID);
                final String urlShortenerKey = request.getParameter(UrlShortenerFactory.PROPERTY_KEY);
                final String urlShortenerTarget = request.getParameter(UrlShortenerFactory.PROPERTY_TARGET);

                if(uuid != null && !uuid.isEmpty()) { // editing existing url shortener
                    final UrlShortener urlShortener = new UrlShortener(uuid, urlShortenerKey, urlShortenerTarget);
                    UrlShortenerFactory.editUrlShortener(urlShortener);
                } else { // adding a new url shortener
                    final UrlShortener urlShortener = new UrlShortener(urlShortenerKey, urlShortenerTarget);
                    final Key key = UrlShortenerFactory.addUrlShortener(urlShortener);
                    uuid = KeyFactory.keyToString(key);
                    log.info("Added new UrlShortener with UUID: " + uuid);
                }

                final String redirectUrl = request.getParameter("redirect");
                response.sendRedirect(redirectUrl);
            }
        }
    }
}