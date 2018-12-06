<%@ page import="org.codecyprus.th.model.Timed" %>
<%@ page import="org.codecyprus.th.db.TimedFactory" %>
<%@ page import="org.codecyprus.th.model.TreasureHunt" %>
<%@ page import="org.codecyprus.th.db.TreasureHuntFactory" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%--
  User: Nearchos
  Date: 06-Dec-18
  Time: 10:31 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%
        final UserService userService = UserServiceFactory.getUserService();
        final boolean signedIn = userService.getCurrentUser() != null;
        boolean isOwner = false;

        final String titleText;
        String bodyText;

        final String timedUuid = request.getParameter("uuid");
        final Timed timed = timedUuid == null || timedUuid.isEmpty() ? null : TimedFactory.getTimed(timedUuid);
        if(timed == null) {
            titleText = "Unknown UUID";
            bodyText = "<h1>Unknown UUID</h1><p>Could not identify a valid timed page for uuid: " + timedUuid + "</p>";
        } else {
            final TreasureHunt treasureHunt = TreasureHuntFactory.getTreasureHunt(timed.getTreasureHuntUuid());
            if(treasureHunt != null) {
                isOwner = userService.getCurrentUser() != null && userService.getCurrentUser().getEmail().equalsIgnoreCase(treasureHunt.getOwnerEmail());
            }
            if(treasureHunt != null && treasureHunt.isActiveNow()) {
                titleText = timed.getTitleText();
                bodyText = timed.getBodyText();
            } else {
                titleText = "Inactive page";
                bodyText = "<h1>Inactive page</h1><p>The selected timed page is not active right now.</p>";
                if(treasureHunt != null) {
                    if(treasureHunt.isNotStarted()) {
                        bodyText += "<p>It starts at <b>" + treasureHunt.getStartsOnAsString() + " UTC</b></p>";
                    } else if(treasureHunt.isFinished()) {
                        bodyText += "<p>It finished on <b>" + treasureHunt.getEndsOnAsString() + " UTC</b></p>";
                    }
                }
            }
        }
    %>
    <title><%=titleText%></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <%=bodyText%>

    <%
        if (!signedIn) {
    %>
    <p><small><a href="<%=userService.createLoginURL(request.getRequestURL().toString())%>">admin sign in</a></small></p>
    <%
        }

        if(isOwner) {
    %>
    <hr/>
    <p><a href="<%=userService.createLogoutURL(request.getRequestURL().toString())%>">admin sign out</a></p>
    <p>Title: <i><%=timed.getTitleText()%></i></p>
    <p>Body:</p>
    <div style="background-color: yellow;">
        <%=timed.getBodyText()%>
    </div>
    <%
        }
    %>

</body>
</html>
