<%@ page import="java.util.Vector" %>
<%@ page import="org.codecyprus.th.admin.DeleteEntity" %>
<%@ page import="java.net.URLEncoder" %><%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 18-Aug-18
  Time: 5:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Users</title>
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
        final Vector<User> users = UserFactory.getAllUsers();
%>
<h1>Users</h1>

<table border="1">
    <tr>
        <th>UUID</th>
        <th>Email</th>
        <th>Nickname</th>
        <th>Is Admin</th>
        <th></th>
    </tr>

    <%
        for(final User thisUser : users) {

    %>
    <tr>
        <td><%=thisUser.getUuid() %></td>
        <td><%= thisUser.getEmail() %></td>
        <td><%= thisUser.getNickname() %></td>
        <td><%= thisUser.isAdmin() %></td>
        <td>
            <form action="delete-entity">
                <div><input type="submit" value="Delete" /></div>
                <input type="hidden" name="<%= UserFactory.PROPERTY_UUID %>" value="<%=thisUser.getUuid()%>"/>
                <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("users", "UTF-8") %>"/>
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
