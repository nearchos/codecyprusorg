/*
 * This file is part of UCLan-THC server.
 *
 *     UCLan-THC server is free software: you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     UCLan-THC server is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.codecyprus.th.admin;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.codecyprus.th.db.TreasureHuntFactory;
import org.codecyprus.th.db.UserFactory;
import org.codecyprus.th.model.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 12:09
 */
public class DeleteEntity extends HttpServlet
{
    public static final Logger log = Logger.getLogger("codecyprus-th");

    public static final String REDIRECT_URL = "redirect-url";

    private static final UserService userService = UserServiceFactory.getUserService();
    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

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
                // delete entity
                final String uuid = request.getParameter(TreasureHuntFactory.PROPERTY_UUID);
                log.info("Deleting entity with UUID: " + uuid);
                datastoreService.delete(KeyFactory.stringToKey(uuid));
                memcacheService.delete(uuid); // invalidate cache entry

                // redirecting to requested url
                final String redirectUrl = request.getParameter(REDIRECT_URL);
                response.sendRedirect(URLDecoder.decode(redirectUrl, "UTF-8"));
            }
        }
    }
}