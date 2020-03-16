<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.codecyprus.th.model.TreasureHunt" %>
<%@ page import="java.util.Vector" %>
<%@ page import="org.codecyprus.th.db.TreasureHuntFactory" %>
<%@ page import="org.codecyprus.th.admin.DeleteEntity" %>
<%@ page import="org.codecyprus.th.model.Visibility" %>
<%@ page import="org.codecyprus.th.db.ConfiguredQuestionFactory" %>
<%@ page import="java.util.ArrayList" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 13-Aug-18
  Time: 12:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>


<style>
    .tooltip {
        position: relative;
        display: inline-block;
        border-bottom: 1px dotted black;
    }

    .tooltip .tooltiptext {
        visibility: hidden;
        background-color: black;
        color: #fff;
        text-align: center;
        border-radius: 6px;
        padding: 5px 10px;

        /* Position the tooltip */
        position: absolute;
        z-index: 1;
    }

    .tooltip:hover .tooltiptext {
        visibility: visible;
    }
</style>

<head>
    <title>Admin - List of TreasureHunts</title>
</head>
<body>

<%@ include file="Authenticate.jsp" %>

<%
    if(user == null) {
%>
You are not logged in!
<%
    } else if(!isAdmin) {
%>
<p><%=user.getEmail()%>, you are not admin!</p>
<%
    } else {
        final boolean includeFinished = "on".equalsIgnoreCase(request.getParameter("include-finished"));
        final Vector<TreasureHunt> treasureHunts = TreasureHuntFactory.getAllTreasureHunts(includeFinished);
%>
    <h1>Treasure Hunts</h1>

    <form action="treasure-hunts">
        Includes finished Treasure Hunts: <%=includeFinished%>
        <input type="hidden" name="include-finished" value="<%=!includeFinished ? "on" : "off"%>">
        <input type="submit" value="Flip"/>
    </form>

    <p>Number of categories: <%=treasureHunts.size()%></p>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Num Of Questions</th>
            <th>Owner Email</th>
            <th>Secret Code</th>
            <th>Salt</th>
            <th>Visibility</th>
            <th>Starts On</th>
            <th>Ends On</th>
            <th>Max Duration</th>
            <th>Shuffled</th>
            <th>Requires Authentication</th>
            <th>Email Results</th>
            <th>Has Prize</th>
            <th>Sessions</th>
            <th>Live Map</th>
            <th></th>
            <th></th>
        </tr>

<%
        for(final TreasureHunt treasureHunt : treasureHunts) {
            final String uuid = treasureHunt.getUuid();
            final String trimmedUuid = uuid.length() > 8 ? uuid.substring(uuid.length() - 8) : uuid;
            int numOfQuestions = ConfiguredQuestionFactory.getNumOfConfiguredQuestionsForTreasureHunt(uuid);
            final String categoryColor = treasureHunt.isActiveNow() ? "green" : treasureHunt.hasFinished() ? "red" : "blue";
            final String salt = treasureHunt.getSalt();
            final String shortenSalt = "undefined".equalsIgnoreCase(salt) ? "undefined" : salt.substring(Math.max(0, salt.length()-4));
%>
        <tr>
            <td><a href="treasure-hunt?uuid=<%= treasureHunt.getUuid() %>"><%=trimmedUuid%></a></td>
            <td><%= treasureHunt.getName() %></td>
            <td><%= treasureHunt.getDescription() %></td>
            <td><%= numOfQuestions %></td>
            <td><%= treasureHunt.getOwnerEmail() %></td>
            <td><div class="tooltip">...<span class="tooltiptext">'<%= treasureHunt.getSecretCode() %>'</span></div></td>
            <td><div class="tooltip"><%= shortenSalt %><span class="tooltiptext">'<%= salt %>'</span></div></td>
            <td><%= treasureHunt.getVisibility().name() %></td>
            <td style="color:<%=categoryColor%>"><%= treasureHunt.getStartsOnAsString() %></td>
            <td style="color:<%=categoryColor%>"><%= treasureHunt.getEndsOnAsString() %></td>
            <td>
                <%= String.format("%.1f s", treasureHunt.getMaxDuration()/1000d) %> /
                <%= String.format("%.1f m", treasureHunt.getMaxDuration()/60000d) %>
            </td>
            <td><%= treasureHunt.isShuffled() %></td>
            <td><%= treasureHunt.isRequiresAuthentication() %></td>
            <td><%= treasureHunt.isEmailResults() %></td>
            <td><%= treasureHunt.isHasPrize() %></td>
            <td>
                <div>
                    <input type="button" value="Sessions" onclick="window.open('sessions?th-uuid=<%=treasureHunt.getUuid()%>')" />
                </div>
            </td>
            <td>
                <div>
                    <input type="button" value="Live map" onclick="window.open('/th/leaderboard/map?treasure-hunt-id=<%=treasureHunt.getUuid()%>')" />
                </div>
            </td>
            <td>
                <form action="treasure-hunt">
                    <div><input type="submit" value="Edit" /></div>
                    <input type="hidden" name="<%= TreasureHuntFactory.PROPERTY_UUID %>" value="<%=uuid%>"/>
                </form>
            </td>
            <td>
                <form action="delete-entity">
                    <div><input type="submit" value="Delete" /></div>
                    <input type="hidden" name="<%= TreasureHuntFactory.PROPERTY_UUID %>" value="<%=uuid%>"/>
                    <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("treasure-hunts", "UTF-8") %>"/>
                </form>
            </td>
        </tr>
<%
        }

        final long now = System.currentTimeMillis();
        final long SEVEN_DAYS = 7L * 24L * 60L * 60L * 1000L;
        final long THIRTY_MINUTES = 30L * 60L * 1000L;
%>
    </table>

    <hr/>

    <h2>Add new Treasure Hunt</h2>

    <form action="add-treasure-hunt" method="post" onsubmit="submitButton.disabled = true; return true;">
        <table>
            <tr>
                <td>Name</td>
                <td><input type="text" name="<%= TreasureHuntFactory.PROPERTY_NAME%>" required/></td>
            </tr>
            <tr>
                <td>Description</td>
                <td><input type="text" name="<%= TreasureHuntFactory.PROPERTY_DESCRIPTION%>" required/></td>
            </tr>
            <tr>
                <td>Owner Email</td>
                <td><%= user.getEmail() %></td>
            </tr>
            <tr>
                <td>Secret Code</td>
                <td><input type="text" name="<%= TreasureHuntFactory.PROPERTY_SECRET_CODE%>"/></td>
            </tr>
            <tr>
                <td>Visibility</td>
                <td>
                    <select name="<%= TreasureHuntFactory.PROPERTY_VISIBILITY %>">
<%
        for(final Visibility visibility : Visibility.values()) {
%>
<option value="<%=visibility.name()%>"> <%= visibility.name() %> </option>
<%
        }
%>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Starts On</td>
                <td><input type="datetime-local" name="<%= TreasureHuntFactory.PROPERTY_STARTS_ON%>" value="<%= TreasureHunt.SIMPLE_DATE_FORMAT.format(new Date(now)) %>"/></td>
            </tr>
            <tr>
                <td>Ends on</td>
                <td><input type="datetime-local" name="<%= TreasureHuntFactory.PROPERTY_ENDS_ON%>" value="<%= TreasureHunt.SIMPLE_DATE_FORMAT.format(new Date(now+SEVEN_DAYS)) %>"/></td>
            </tr>
            <tr>
                <td>Max Duration</td>
                <td><input type="text" name="<%= TreasureHuntFactory.PROPERTY_MAX_DURATION%>" value="<%=THIRTY_MINUTES%>"/></td>
            </tr>
            <tr>
                <td>Shuffled</td>
                <td><input type="checkbox" name="<%= TreasureHuntFactory.PROPERTY_SHUFFLED%>"/></td>
            </tr>
            <tr>
                <td>Requires Authentication</td>
                <td><input type="checkbox" name="<%= TreasureHuntFactory.PROPERTY_REQUIRES_AUTHENTICATION%>"/></td>
            </tr>
            <tr>
                <td>Email Results</td>
                <td><input type="checkbox" name="<%= TreasureHuntFactory.PROPERTY_REQUIRES_AUTHENTICATION%>"/></td>
            </tr>
            <tr>
                <td>Has Prize</td>
                <td><input type="checkbox" name="<%= TreasureHuntFactory.PROPERTY_HAS_PRIZE%>"/></td>
            </tr>
            <tr><td colspan="2"><i>Please note that all times are in <a href="http://en.wikipedia.org/wiki/UTC">UTC (Coordinated Universal Time)</a></i></td></tr>
        </table>
        <div><input type="submit" name="submitButton" value="Add Treasure Hunt" /></div>
    </form>

<%
    }
%>
</body>
</html>