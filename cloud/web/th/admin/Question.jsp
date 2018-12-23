<%@ page import="org.codecyprus.th.model.Question" %>
<%@ page import="org.codecyprus.th.db.QuestionFactory" %>
<%@ page import="org.codecyprus.th.model.QuestionType" %>
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
    <title>Admin - Question</title>
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
        final Question question = QuestionFactory.getQuestion(uuid);
        assert question != null;
%>
    <h1>Question</h1>

    <form action="edit-question" method="post" onsubmit="editQuestionButton.disabled=true; return true;">
        <table>
            <tr>
                <th>UUID</th>
                <td><input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID%>" value="<%=uuid%>"/> <%=uuid%> </td>
            </tr>
            <tr>
                <th>Question Text</th>
                <td>
                    <label>
                        <textarea rows="20" cols="120" name="<%= QuestionFactory.PROPERTY_QUESTION_TEXT%>" title="Question Text"><%=question.getQuestionText()%></textarea>
                    </label>
                </td>
            </tr>
            <tr>
                <th>Question Type</th>
                <td>
                    <select name="<%= QuestionFactory.PROPERTY_QUESTION_TYPE %>" title="Question Type">
                        <%
                            for(final QuestionType questionType : QuestionType.values()) {
                        %>
                        <option value="<%=questionType.name()%>" <%=questionType.equals(question.getQuestionType()) ? "selected" : ""%>> <%=questionType.name()%> </option>
                        <%
                            }
                        %>
                    </select>
                </td>
            </tr>
            <tr>
                <th>Correct Answer</th>
                <td><input type="text" name="<%= QuestionFactory.PROPERTY_CORRECT_ANSWER %>" title="Correct Answer" value="<%=question.getCorrectAnswer()%>"/></td>
            </tr>
            <tr>
                <th>Creator Email</th>
                <td><input type="hidden" name="<%= QuestionFactory.PROPERTY_CREATOR_EMAIL %>" title="Creator Email"/> <%= user.getEmail() %> </td>
            </tr>
            <tr>
                <th>Shared</th>
                <td><input type="checkbox" name="<%= QuestionFactory.PROPERTY_SHARED%>" title="Shared" <%=question.isShared() ? "checked" : ""%>/></td>
            </tr>
        </table>
        <div><input type="submit" value="Save changes" name="editQuestionButton"/></div>
        <input type="hidden" name="redirect" value="questions" />
    </form>
<%
    }
%>

</body>
</html>
