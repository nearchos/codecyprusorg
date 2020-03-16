<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.codecyprus.th.admin.DeleteEntity" %>
<%@ page import="org.codecyprus.th.model.Timed" %>
<%@ page import="org.codecyprus.th.db.TimedFactory" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.codecyprus.th.db.TreasureHuntFactory" %>
<%@ page import="java.util.Vector" %>
<%@ page import="org.codecyprus.th.model.TreasureHunt" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 7-Mar-20
  Time: 9:15 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin - List of Timed Questions</title>
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
        final ArrayList<Timed> timedQuestions = TimedFactory.getAllTimed();
%>
    <h1>Timed Questions</h1>

    <p>Number of timed questions: <%=timedQuestions.size()%></p>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>TH ID</th>
            <th>Title</th>
            <th>Body</th>
            <th></th>
            <th></th>
        </tr>

<%
        final Vector<TreasureHunt> allTreasureHunts = TreasureHuntFactory.getAllTreasureHunts(true);
        final Map<String,String> treasureHuntIdsToNames = new HashMap<>();
        for(final TreasureHunt treasureHunt : allTreasureHunts) {
            treasureHuntIdsToNames.put(treasureHunt.getUuid(), treasureHunt.getName());
        }

        for(final Timed timedQuestion : timedQuestions) {
            final String uuid = timedQuestion.getUuid();
            final String trimmedUuid = uuid.length() > 8 ? uuid.substring(uuid.length() - 8) : uuid;
            final String thUUID = timedQuestion.getTreasureHuntUuid();
%>
        <tr>
            <td><a href="timed?uuid=<%= uuid %>"><%=trimmedUuid%></a></td>
            <td>...<%= thUUID.substring(Math.max(0, thUUID.length() - 4)) %> <br/> (<%=treasureHuntIdsToNames.get(thUUID)%>)</td>
            <td><%= timedQuestion.getTitleText() %></td>
            <td><%= timedQuestion.getBodyText() %></td>
            <td>
                <form action="timedQuestion">
                    <div><input type="submit" value="Edit" /></div>
                    <input type="hidden" name="<%= TimedFactory.PROPERTY_UUID %>" value="<%=uuid%>"/>
                </form>
            </td>
            <td>
                <form action="delete-entity">
                    <div><input type="submit" value="Delete" /></div>
                    <input type="hidden" name="<%= TimedFactory.PROPERTY_UUID %>" value="<%=uuid%>"/>
                    <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("timed-questions", "UTF-8") %>"/>
                </form>
            </td>
        </tr>
<%
        }
%>
    </table>

    <hr/>

    <h2>Add Timed</h2>

    <form action="add-timed" method="post" onsubmit="addTimedQuestionButton.disabled = true; return true;">
        <table>
            <tr>
                <th>Treasure Hunt</th>
                <td>
                    <select name="<%= TimedFactory.PROPERTY_TREASURE_HUNT_ID %>">
                        <%
                            for(final TreasureHunt treasureHunt : allTreasureHunts) {
                        %>
                        <option value="<%=treasureHunt.getUuid() %>"> <%= treasureHunt.getName() %> </option>
                        <%
                            }
                        %>
                    </select>
                </td>
            </tr>
            <tr>
                <th>Timed Question Title</th>
                <td><input type="text" name="<%= TimedFactory.PROPERTY_TITLE_TEXT%>" /></td>
            </tr>
            <tr>
                <th>Timed Question Body</th>
                <td><input type="text" name="<%= TimedFactory.PROPERTY_BODY_TEXT%>" /></td>
            </tr>
        </table>
        <div><input type="submit" value="Add Timed question" name="addTimedQuestionButton"/></div>
        <input type="hidden" name="redirect" value="timed-questions" />
    </form>

<%
    }
%>
</body>
</html>