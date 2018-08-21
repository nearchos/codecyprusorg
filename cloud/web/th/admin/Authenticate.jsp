<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="org.codecyprus.th.db.UserFactory" %>
<%@ page import="org.codecyprus.th.model.User" %>
<%--
  ~ This file is part of UCLan-THC server.
  ~
  ~     UCLan-THC server is free software: you can redistribute it and/or
  ~     modify it under the terms of the GNU General Public License as
  ~     published by the Free Software Foundation, either version 3 of
  ~     the License, or (at your option) any later version.
  ~
  ~     UCLan-THC server is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<%--
  Date: 8/17/12
  Time: 10:41 AM
--%>
    <h1> Code Cyprus - Treasure Hunt </h1>
<%
    final UserService userService = UserServiceFactory.getUserService();
    final String email = userService.getCurrentUser() == null ? "Unknown" : userService.getCurrentUser().getEmail();
    final String nickname = userService.getCurrentUser() == null ? "Nickname" : userService.getCurrentUser().getNickname();
    boolean isAdmin = false;
    User user = null;
    if (userService.getCurrentUser() == null) {
%>
    <p>You need to <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> to use this service.</p>
<%
    } else {
        user = UserFactory.getUserByEmail(email);
        if(user == null) {
            user = new User(email, nickname, false);
            UserFactory.addUser(user);
        }
        isAdmin = user.isAdmin();
%>
    <p><img src="/favicon.ico" alt="UCLan"/> Signed in as: <%= nickname %> <b> <%= isAdmin ? "(admin)" : "" %> </b> [<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>]</p>
<%
    }
%>