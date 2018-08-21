<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.codecyprus.th.model.TreasureHunt" %>
<%@ page import="org.codecyprus.th.db.TreasureHuntFactory" %>
<%@ page import="org.codecyprus.th.model.ConfiguredQuestion" %>
<%@ page import="org.codecyprus.th.db.ConfiguredQuestionFactory" %>
<%@ page import="org.codecyprus.th.model.Session" %>
<%@ page import="org.codecyprus.th.db.SessionFactory" %>
<%@ page import="org.codecyprus.th.admin.DeleteEntity" %><%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 17-Aug-18
  Time: 5:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin - Sessions</title>
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
        final String treasureHuntId = request.getParameter("th-uuid");
        final TreasureHunt treasureHunt = TreasureHuntFactory.getTreasureHunt(treasureHuntId);
        assert treasureHunt != null;
        final ArrayList<ConfiguredQuestion> configuredQuestions = ConfiguredQuestionFactory.getConfiguredQuestionsForTreasureHunt(treasureHuntId);
        final Map<String,Long> configuredQuestionIdToSeqNumberMap = new HashMap<>();
        for(final ConfiguredQuestion configuredQuestion : configuredQuestions) {
            configuredQuestionIdToSeqNumberMap.put(configuredQuestion.getUuid(), configuredQuestion.getSeqNumber());
        }
        final Vector<Session> sessions = SessionFactory.getSessionsByTreasureHuntId(treasureHuntId);
%>
    <h1>Sessions</h1>

    <p>Treasure Hunt: <%=treasureHunt.getShortUuid()%></p>
    <p>Number of sessions: <%=sessions.size()%></p>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>Player Name</th>
            <th>App Name</th>
            <th>Start Time</th>
            <th>Score</th>
            <th>Question Seq Numbers</th>
            <th>Current Index</th>
            <th></th>
        </tr>
<%
        for(final Session session1 : sessions) {
            final ArrayList<String> configuredQuestionUuids = session1.getConfiguredQuestionUuids();
            final ArrayList<Long> configuredQuestionSeqNumbers = new ArrayList<>();
            for(final String configuredQuestionUuid : configuredQuestionUuids) {
                configuredQuestionSeqNumbers.add(configuredQuestionIdToSeqNumberMap.get(configuredQuestionUuid));
            }
%>
        <tr>
            <td><%=session1.getShortUuid()%></td>
            <td><%=session1.getPlayerName()%></td>
            <td><%=session1.getAppName()%></td>
            <td><%=session1.getStartTimeFormatted()%></td>
            <td><%=session1.getScore()%></td>
            <td><%=configuredQuestionSeqNumbers%></td>
            <td><%=session1.getCurrentConfiguredQuestionIndex()%></td>
            <td>
                <form action="delete-entity">
                    <div><input type="submit" value="Delete" /></div>
                    <input type="hidden" name="<%= SessionFactory.PROPERTY_UUID %>" value="<%=session1.getUuid()%>"/>
                    <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("sessions?th-uuid=" + treasureHuntId, "UTF-8") %>"/>
                </form>
            </td>
        </tr>
<%
        }
%>
    </table>
<%
    }
%>
</body>
</html>
