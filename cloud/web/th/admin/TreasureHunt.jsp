<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Vector" %>
<%@ page import="org.codecyprus.th.db.TreasureHuntFactory" %>
<%@ page import="org.codecyprus.th.admin.DeleteEntity" %>
<%@ page import="org.codecyprus.th.db.ConfiguredQuestionFactory" %>
<%@ page import="org.codecyprus.th.db.QuestionFactory" %>
<%@ page import="org.codecyprus.th.model.*" %>
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
  User: Nearchos Paspallis
  Date: 11/09/13
  Time: 11:59
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
        <title>Admin - Treasure Hunt</title>
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
        You are not admin!
<%
    } else {
        String key = request.getParameter(TreasureHuntFactory.PROPERTY_UUID);
        final TreasureHunt treasureHunt = TreasureHuntFactory.getTreasureHunt(key);
        assert treasureHunt != null;

        final long THIRTY_MINUTES = 30L * 60L * 1000L;

%>
        <p><a href="treasure-hunts">Back to all Treasure Hunts</a></p>

        <hr/>
        <h1>Name: <%= treasureHunt.getName() %></h1>

        <p><b>UUID</b>: <%= treasureHunt.getUuid() %></p>
        <p><b>Name</b>: <%= treasureHunt.getName() %></p>
        <p><b>Description</b>: <%= treasureHunt.getDescription() %></p>
        <p><b>Owner Email</b>: <%= treasureHunt.getOwnerEmail() %></p>
        <p><b>Visibility</b>: <%= treasureHunt.getVisibility().name() %></p>
        <p><b>Starts On</b>: <%= treasureHunt.getStartsOnAsString() %></p>
        <p><b>Ends On</b>: <%= treasureHunt.getEndsOnAsString() %></p>
        <p><b>Max Duration</b>: <%= treasureHunt.getMaxDuration() %></p>
        <p><b>Shuffled</b>: <%= treasureHunt.isShuffled() %></p>
        <p><b>Requires Authentication</b>: <%= treasureHunt.isRequiresAuthentication() %></p>
        <p><b>Email Results</b>: <%= treasureHunt.isEmailResults() %></p>
        <p><b>Is active now</b>: <%= treasureHunt.isActiveNow() %></p>

        <hr/>

        <p>View <a href="sessions?th-uuid=<%=treasureHunt.getUuid()%>">sessions</a></p>

        <hr/>

        <form action="edit-treasure-hunt" method="post" onsubmit="editButton.disabled=true; return true;">
            <table>
                <tr>
                    <td>Uuid</td>
                    <td><input type="hidden" name="<%= TreasureHuntFactory.PROPERTY_UUID%>" value="<%=treasureHunt.getUuid()%>" /> <%=treasureHunt.getUuid()%></td>
                </tr>
                <tr>
                    <td>Name</td>
                    <td><input type="text" name="<%= TreasureHuntFactory.PROPERTY_NAME %>" value="<%=treasureHunt.getName()%>" required/></td>
                </tr>
                <tr>
                    <td>Description</td>
                    <td><input type="text" name="<%= TreasureHuntFactory.PROPERTY_DESCRIPTION%>" value="<%=treasureHunt.getDescription()%>"/></td>
                </tr>
                <tr>
                    <td>Owner Email</td>
                    <td><input type="hidden" name="<%= TreasureHuntFactory.PROPERTY_OWNER_EMAIL %>" value="<%=treasureHunt.getOwnerEmail()%>" /> <%=treasureHunt.getOwnerEmail()%></td>
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
                <%--<tr>--%>
                    <%--<td>NUM OF QUESTIONS</td>--%>
                    <%--<td><%= questions.size() %></td>--%>
                <%--</tr>--%>
                <tr>
                    <td>Starts On</td>
                    <td><input type="datetime-local" name="<%= TreasureHuntFactory.PROPERTY_STARTS_ON%>" value="<%= TreasureHunt.SIMPLE_DATE_FORMAT.format(new Date(treasureHunt.getStartsOn())) %>"/></td>
                </tr>
                <tr>
                    <td>Ends on</td>
                    <td><input type="datetime-local" name="<%= TreasureHuntFactory.PROPERTY_ENDS_ON%>" value="<%= TreasureHunt.SIMPLE_DATE_FORMAT.format(new Date(treasureHunt.getEndsOn())) %>"/></td>
                </tr>
                <tr>
                    <td>Max Duration</td>
                    <td><input type="text" name="<%= TreasureHuntFactory.PROPERTY_MAX_DURATION%>" value="<%=treasureHunt.getMaxDuration()%>"/></td>
                </tr>
                <tr>
                    <td>Shuffled</td>
                    <td><input type="checkbox" name="<%= TreasureHuntFactory.PROPERTY_SHUFFLED%>" <%=treasureHunt.isShuffled() ? "checked" : ""%>/></td>
                </tr>
                <tr>
                    <td>Requires Authentication</td>
                    <td><input type="checkbox" name="<%= TreasureHuntFactory.PROPERTY_REQUIRES_AUTHENTICATION%>" <%=treasureHunt.isRequiresAuthentication() ? "checked" : ""%>/></td>
                </tr>
                <tr>
                    <td>Email Results</td>
                    <td><input type="checkbox" name="<%= TreasureHuntFactory.PROPERTY_EMAIL_RESULTS%>" <%=treasureHunt.isEmailResults() ? "checked" : ""%>/></td>
                </tr>
                <tr>
                    <td>Has Prize</td>
                    <td><input type="checkbox" name="<%= TreasureHuntFactory.PROPERTY_HAS_PRIZE%>" <%=treasureHunt.isHasPrize() ? "checked" : ""%>/></td>
                </tr>
                <tr>
                    <td>Is Active Now</td>
                    <td><%= treasureHunt.isActiveNow() %></td>
                </tr>
                <tr><td colspan="2"><i>Please note that all times are in <a href="http://en.wikipedia.org/wiki/UTC">UTC (Coordinated Universal Time)</a></i></td></tr>
            </table>
            <div><input type="submit" value="Edit treasure hunt" name="editButton" /></div>
        </form>

        <hr/>
        <form action="delete-entity" onsubmit="deleteButton.disabled = true; return true;">
            <div><input type="submit" value="Delete category" name="deleteButton"/></div>
            <input type="hidden" name="<%= TreasureHuntFactory.PROPERTY_UUID %>" value="<%= treasureHunt.getUuid() %>"/>
            <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("treasure-hunts", "UTF-8") %>"/>
        </form>

        <hr/>

<%
        final ArrayList<ConfiguredQuestion> configuredQuestions = ConfiguredQuestionFactory.getConfiguredQuestionsForTreasureHunt(treasureHunt.getUuid());
%>
        <h2>Configured Questions</h2>
        <p>Contains <%=configuredQuestions.size()%> questions</p>

        <table border="1">
            <tr>
                <th>UUID</th>
                <th>Question</th>
                <th>Seq Number</th>
                <th>Correct Score</th>
                <th>Wrong Score</th>
                <th>Skip Score</th>
                <th>Can Be Skipped</th>
                <th>Latitude</th>
                <th>Longitude</th>
                <th>Distance Threshold</th>
                <th></th>
                <th></th>
            </tr>
<%
        for(final ConfiguredQuestion configuredQuestion : configuredQuestions) {
            final String uuid = configuredQuestion.getUuid();
            final String shortUuid = uuid.length() > 8 ? uuid.substring(uuid.length() - 8) : uuid;
            final Question question = QuestionFactory.getQuestion(configuredQuestion.getQuestionUuid());
            assert question != null;
%>
            <tr>
                <td><div class="tooltip"><%=shortUuid%><span class="tooltiptext"><%=uuid%></span></div></td>
                <td><%= question.getQuestionText() %> (<%= question.getQuestionType().name() %>) with correct answer '<%=question.getCorrectAnswer()%>' </td>
                <td><%= configuredQuestion.getSeqNumber() %></td>
                <td><%= configuredQuestion.getCorrectScore() %></td>
                <td><%= configuredQuestion.getWrongScore() %></td>
                <td><%= configuredQuestion.getSkipScore() %></td>
                <td><%= configuredQuestion.isCanBeSkipped() %></td>
                <td><%= configuredQuestion.getLatitude() %></td>
                <td><%= configuredQuestion.getLongitude() %></td>
                <td><%= configuredQuestion.getDistanceThreshold() %></td>
                <td>
                    <form action="configured-question">
                        <div><input type="submit" value="Edit" /></div>
                        <input type="hidden" name="<%= ConfiguredQuestionFactory.PROPERTY_UUID %>" value="<%= uuid %>"/>
                    </form>
                </td>
                <td>
                    <form action="delete-entity">
                        <div><input type="submit" value="Delete" /></div>
                        <input type="hidden" name="<%= ConfiguredQuestionFactory.PROPERTY_UUID %>" value="<%= uuid %>"/>
                        <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("treasure-hunt?uuid=" + treasureHunt.getUuid(), "UTF-8") %>"/>
                    </form>
                </td>
            </tr>
<%
        }

        final Vector<Question> sharedQuestions = QuestionFactory.getQuestions(true);
%>
        </table>

        <hr/>

        <h2>Add ConfiguredQuestion</h2>

        <form action="add-configured-question" method="post" onsubmit="addConfiguredQuestionButton.disabled=true; return true;">
            <table>
                <tr>
                    <th>Seq Number</th>
                    <td><input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_SEQ_NUMBER%>" /></td>
                </tr>
                <tr>
                    <th>Question</th>
                    <td>
                        <select name="<%= ConfiguredQuestionFactory.PROPERTY_QUESTION_ID%>">
<%
        for(final Question sharedQuestion : sharedQuestions) {
%>
                             <option value="<%=sharedQuestion.getUuid()%>"> <%= sharedQuestion.getQuestionText() %> (<%= sharedQuestion.getQuestionType().name()%>) with correct answer '<%= sharedQuestion.getCorrectAnswer()%>'</option>
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
                    <td><input type="checkbox" name="<%= ConfiguredQuestionFactory.PROPERTY_CAN_BE_SKIPPED%>" /></td>
                </tr>
                <tr>
                    <th>Latitude</th>
                    <td><input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_LATITUDE %>" value="0.0" /></td>
                </tr>
                <tr>
                    <th>Longitude</th>
                    <td><input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_LONGITUDE %>" value="0.0" /></td>
                </tr>
                <tr>
                    <th>Distance Threshold</th>
                    <td><input type="number" name="<%= ConfiguredQuestionFactory.PROPERTY_DISTANCE_THRESHOLD%>" value="0.0" /></td>
                </tr>
            </table>
            <div><input type="submit" value="Add configured question" name="addConfiguredQuestionButton"/></div>
            <input type="hidden" name="<%= ConfiguredQuestionFactory.PROPERTY_TREASURE_HUNT_ID %>" value="<%= treasureHunt.getUuid() %>" />
            <input type="hidden" name="redirect" value="treasure-hunt?uuid=<%= treasureHunt.getUuid() %>" />
        </form>

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
    <input type="hidden" name="<%= ConfiguredQuestionFactory.PROPERTY_TREASURE_HUNT_ID %>" value="<%= treasureHunt.getUuid() %>" />
    <input type="hidden" name="redirect" value="treasure-hunt?uuid=<%= treasureHunt.getUuid() %>" />
</form>

<p><a href="questions">View or edit questions</a></p>
<%
    }
%>
    </body>

</html>