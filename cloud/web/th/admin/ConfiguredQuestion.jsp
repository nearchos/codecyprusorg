<%@ page import="org.codecyprus.th.model.Question" %>
<%@ page import="org.codecyprus.th.db.QuestionFactory" %>
<%@ page import="org.codecyprus.th.model.QuestionType" %>
<%@ page import="org.codecyprus.th.model.ConfiguredQuestion" %>
<%@ page import="org.codecyprus.th.db.ConfiguredQuestionFactory" %>
<%@ page import="java.util.Vector" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 17-Aug-18
  Time: 7:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Admin - Configured Question</title>
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
        final String uuid = request.getParameter("uuid");
        final ConfiguredQuestion configuredQuestion = ConfiguredQuestionFactory.getConfiguredQuestion(uuid);
        assert configuredQuestion != null;
%>
    <h1>Configured Question</h1>

    <form action="edit-configured-question" method="post" onsubmit="editConfiguredQuestionButton.disabled=true; return true;">
        <table>
            <tr>
                <th>UUID</th>
                <td><input type="hidden" name="<%= ConfiguredQuestionFactory.PROPERTY_UUID%>" value="<%=uuid%>"/> <%=uuid%> </td>
            </tr>
            <tr>
                <th>Treasure Hunt UUID</th>
                <td><input type="hidden" name="<%= ConfiguredQuestionFactory.PROPERTY_TREASURE_HUNT_ID%>" value="<%=configuredQuestion.getTreasureHuntUuid()%>"/> <%=configuredQuestion.getTreasureHuntUuid()%> </td>
            </tr>
            <tr>
                <th>Seq Number</th>
                <td>
                    <label>
                        <input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_SEQ_NUMBER%>" value="<%=configuredQuestion.getSeqNumber()%>">
                    </label>
                </td>
            </tr>
            <tr>
                <th>Question</th>
                <td>
                    <select name="<%= ConfiguredQuestionFactory.PROPERTY_QUESTION_ID%>">
<%
    final Vector<Question> allQuestions = QuestionFactory.getQuestions(true);
    for(final Question sharedQuestion : allQuestions) {
        final boolean selected = configuredQuestion.getQuestionUuid().equalsIgnoreCase(sharedQuestion.getUuid());
%>
                        <option value="<%=sharedQuestion.getUuid()%>" <%=selected ? "selected" : ""%>> <%= sharedQuestion.getQuestionText() %> (<%= sharedQuestion.getQuestionType().name()%>) with correct answer '<%= sharedQuestion.getCorrectAnswer()%>'</option>
<%
    }
%>
                    </select>
                    <a href="questions">Add or view questions</a>
                </td>
            </tr>
            <tr>
                <th>Correct Score</th>
                <td><input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_CORRECT_SCORE %>" value="10"/></td>
            </tr>
            <tr>
                <th>Wrong Score</th>
                <td><input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_WRONG_SCORE %>" value="-3"/></td>
            </tr>
            <tr>
                <th>Skip Score</th>
                <td><input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_SKIP_SCORE %>" value="-5"/></td>
            </tr>
            <tr>
                <th>Can Be Skipped</th>
                <td><input type="checkbox" name="<%= ConfiguredQuestionFactory.PROPERTY_CAN_BE_SKIPPED%>" <%=configuredQuestion.isCanBeSkipped() ? "checked"  : ""%>/></td>
            </tr>
            <tr>
                <th>Latitude</th>
                <td><input type="number" min="-90" max ="90" step="0.000001" name="<%= ConfiguredQuestionFactory.PROPERTY_LATITUDE %>" value="0.0" /></td>
            </tr>
            <tr>
                <th>Longitude</th>
                <td><input type="number" min="-180" max="180" step="0.000001" name="<%= ConfiguredQuestionFactory.PROPERTY_LONGITUDE %>" value="0.0" /></td>
            </tr>
            <tr>
                <th>Distance Threshold</th>
                <td><input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_DISTANCE_THRESHOLD%>" value="0.0" /></td>
            </tr>
        </table>
        <div><input type="submit" value="Save changes" name="editConfiguredQuestionButton"/></div>
        <input type="hidden" name="redirect" value="treasure-hunt?uuid=<%= configuredQuestion.getTreasureHuntUuid() %>" />

    </form>
<%
    }
%>

</body>
</html>
