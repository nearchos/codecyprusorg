package org.codecyprus.th.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.codecyprus.th.db.ParameterFactory;
import org.codecyprus.th.db.UserFactory;
import org.codecyprus.th.model.Parameter;
import org.codecyprus.th.model.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class AddOrEditParameterServlet extends HttpServlet {

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
                String uuid = request.getParameter(ParameterFactory.PROPERTY_UUID);
                final String parameterKey = request.getParameter(ParameterFactory.PROPERTY_KEY);
                final String parameterValue = request.getParameter(ParameterFactory.PROPERTY_VALUE);

                if(uuid != null && !uuid.isEmpty()) { // editing existing parameter
                    ParameterFactory.editParameter(uuid, parameterKey, parameterValue);
                } else { // adding a new parameter
                    final Key key = ParameterFactory.addParameter(parameterKey, parameterValue);
                    uuid = KeyFactory.keyToString(key);
                    log.info("Added new Parameter with UUID: " + uuid);
                }

                final String redirectUrl = request.getParameter("redirect");
                response.sendRedirect(redirectUrl);
            }
        }
    }
}
