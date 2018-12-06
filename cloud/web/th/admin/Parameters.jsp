<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Vector" %>
<%@ page import="org.codecyprus.th.admin.DeleteEntity" %>
<%@ page import="org.codecyprus.th.model.Parameter" %>
<%@ page import="org.codecyprus.th.db.ParameterFactory" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 16-Aug-18
  Time: 7:08 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin - Parameters</title>
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
        final Vector<Parameter> parameters = ParameterFactory.getAllParameters();
%>
    <h1>Parameter</h1>

    <p>Number of parameter: <%=parameters.size()%></p>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>Key</th>
            <th>Value</th>
            <th></th>
        </tr>

<%
        for(final Parameter parameter : parameters) {
            final String uuid = parameter.getUuid();
%>
        <tr>
            <td><%= uuid %></td>
            <td><%= parameter.getKey() %></td>
            <td><%= parameter.getValue() %></td>
            <td>
                <form action="delete-entity">
                    <div><input type="submit" value="Delete" /></div>
                    <input type="hidden" name="<%= ParameterFactory.PROPERTY_UUID %>" value="<%=uuid%>"/>
                    <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("parameters", "UTF-8") %>"/>
                </form>
            </td>
        </tr>
<%
        }
%>
    </table>

    <hr/>

    <h2>Add Parameter</h2>

    <form action="add-parameter" method="post" onsubmit="addParameterButton.disabled = true; return true;">
        <table>
            <tr>
                <th>Parameter key</th>
                <td><input type="text" name="<%= ParameterFactory.PROPERTY_KEY%>" /></td>
            </tr>
            <tr>
                <th>Parameter value</th>
                <td><input type="text" name="<%= ParameterFactory.PROPERTY_VALUE%>" /></td>
            </tr>
        </table>
        <div><input type="submit" value="Add parameter" name="addParameterButton"/></div>
        <input type="hidden" name="redirect" value="parameters" />
    </form>

<%
    }
%>
</body>
</html>