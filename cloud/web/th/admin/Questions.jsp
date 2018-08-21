<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.codecyprus.th.model.Question" %>
<%@ page import="org.codecyprus.th.db.QuestionFactory" %>
<%@ page import="java.util.Vector" %>
<%@ page import="org.codecyprus.th.admin.DeleteEntity" %>
<%@ page import="org.codecyprus.th.model.QuestionType" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 16-Aug-18
  Time: 7:08 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin - List of Questions</title>
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
        final Vector<Question> questions = QuestionFactory.getQuestions(true);
%>
    <h1>Questions</h1>

    <p>Number of questions: <%=questions.size()%></p>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>Question text</th>
            <th>Question Type</th>
            <th>Correct Answer</th>
            <th>Creator Email</th>
            <th>Creation Timestamp</th>
            <th>Shared</th>
            <th></th>
            <th></th>
        </tr>

<%
        for(final Question question : questions) {
            final String uuid = question.getUuid();
            final String trimmedUuid = uuid.length() > 8 ? uuid.substring(uuid.length() - 8) : uuid;
%>
        <tr>
            <td><a href="question?uuid=<%= uuid %>"><%=trimmedUuid%></a></td>
            <td><%= question.getQuestionText() %></td>
            <td><%= question.getQuestionType() %></td>
            <td><%= question.getCorrectAnswer() %></td>
            <td><%= question.getCreatorEmail() %></td>
            <td><%= new Date(question.getCreationTimestamp()) %></td>
            <td><%= question.isShared() %></td>
            <td>
                <form action="question">
                    <div><input type="submit" value="Edit" /></div>
                    <input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID %>" value="<%=uuid%>"/>
                </form>
            </td>
            <td>
                <form action="delete-entity">
                    <div><input type="submit" value="Delete" /></div>
                    <input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID %>" value="<%=uuid%>"/>
                    <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("questions", "UTF-8") %>"/>
                </form>
            </td>
        </tr>
<%
        }
%>
    </table>

    <hr/>

    <h2>Add Question</h2>

    <form action="add-question" method="post" onsubmit="addQuestionButton.disabled = true; return true;">
        <table>
            <tr>
                <th>Question Text</th>
                <td><input type="text" name="<%= QuestionFactory.PROPERTY_QUESTION_TEXT%>" /></td>
            </tr>
            <tr>
                <th>Question Type</th>
                <td>
                    <select name="<%= QuestionFactory.PROPERTY_QUESTION_TYPE %>">
<%
    for(final QuestionType questionType : QuestionType.values()) {
%>
                    <option value="<%=questionType.name() %>"> <%= questionType.name() %> </option>
<%
    }
%>
                    </select>
                </td>
            </tr>
            <tr>
                <th>Correct Answer</th>
                <td><input type="text" name="<%= QuestionFactory.PROPERTY_CORRECT_ANSWER %>" /></td>
            </tr>
            <tr>
                <th>Creator Email</th>
                <td><input type="hidden" name="<%= QuestionFactory.PROPERTY_CREATOR_EMAIL %>" /> <%= user.getEmail() %> </td>
            </tr>
            <tr>
                <th>Shared</th>
                <td><input type="checkbox" name="<%= QuestionFactory.PROPERTY_SHARED%>" /></td>
            </tr>
        </table>
        <div><input type="submit" value="Add question" name="addQuestionButton"/></div>
        <input type="hidden" name="redirect" value="questions" />
    </form>

<%
    }
%>
</body>
</html>