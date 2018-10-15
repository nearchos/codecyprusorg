<%@ page import="org.codecyprus.th.model.QuestionType" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="static org.codecyprus.th.api.test.TestListServlet.DEFAULT_NUMBER_OF_TREASURE_HUNTS" %>
<%@ page import="org.codecyprus.th.api.test.TestStartServlet" %>
<%@ page import="static org.codecyprus.th.api.test.TestStartServlet.StartError.INACTIVE" %>
<%@ page import="static org.codecyprus.th.api.test.TestStartServlet.StartError.EMPTY" %>
<%@ page import="static org.codecyprus.th.api.test.TestStartServlet.StartError.PLAYER" %>
<%@ page import="static org.codecyprus.th.api.test.TestStartServlet.StartError.*" %>
<%@ page import="static org.codecyprus.th.api.test.TestQuestionServlet.PARAMETER_COMPLETED" %>
<%@ page import="static org.codecyprus.th.api.test.TestQuestionServlet.PARAMETER_CAN_BE_SKIPPED" %>
<%@ page import="static org.codecyprus.th.api.test.TestQuestionServlet.PARAMETER_REQUIRES_LOCATION" %>
<%@ page import="static org.codecyprus.th.api.test.TestQuestionServlet.*" %>
<%@ page import="org.codecyprus.th.api.test.TestAnswerServlet" %>
<%@ page import="org.codecyprus.th.api.test.TestScoreServlet" %>
<%@ page import="org.codecyprus.th.api.test.TestLeaderboardServlet" %>
<%--
  Created by Nearchos Paspallis
  Date: 11-Oct-18
  Time: 9:52 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <title>Code Cyprus Treasure Hunt: Test API Guide</title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css" integrity="sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb" crossorigin="anonymous">

    <!-- Guide CSS -->
    <link rel="stylesheet" href="th/guide.css" crossorigin="anonymous">
    <!-- Sticky Footer CSS -->
    <link rel="stylesheet" href="th/sticky-footer.css" crossorigin="anonymous">
</head>
<body style="background-color: #ffffd0">

    <nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
        <a class="navbar-brand" href="#"><img src="/th/pirate.png" height="25" alt="Code Cyprus - Pirate API logo" title="Code Cyprus - Pirate API logo"> Treasure Hunt Testing API Guide</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="#intro">Introduction</a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" href="#calls">Calls</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#list"><kbd>/list</kbd></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#start"><kbd>/start</kbd></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#question"><kbd>/question</kbd></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#answer"><kbd>/answer</kbd></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#score"><kbd>/score</kbd></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#leaderboard"><kbd>/leaderboard</kbd></a>
                </li>
            </ul>
        </div>
    </nav>

    <div class="container">
        <div style="height: 64px;"></div>
        <div class="jumbotron" style="background-color: #fdd372">
            <p class="display-4"><img src="/th/pirate_api.png" alt="Code Cyprus - Pirate API logo" title="Code Cyprus - Pirate API logo" height="120"/> Treasure Hunt: Testing API Guide</p>
            <p class="lead">A Code Cyprus project</p>
            <hr class="my-4">
            <p>
                This is version <kbd>1.0.0</kbd> of the Treasure Hunt <i>testing</i> API Guide. It is available online at <code>http://www.codecyprus.org/th/testing</code>.
            </p>
            <p>
                There is also the main Treasure Hunt API Guide, available at <code><a href="/th/guide" target="_blank">http://www.codecyprus.org/th/guide</a></code>.
            </p>
        </div>

        <div class="row">
            <div class="col">
                <a name="intro"></a>
                <div style="height: 58px;"></div>
                <h3>Introduction</h3>
                <p>
                    This service provides facilities for testing apps designed to use the Treasure Hunt API. For more
                    information about the API's scope and goals please see the
                    <a href="/th/guide#intro" target="_blank">API Guide introduction</a>.
                    Like the actual API, this testing backend is developed for and deployed on Google's app-engine.
                </p>
                <p>
                    This <i>testing</i> API aims to complement the actual one by providing the means to test clients
                    developed to use the various calls. For example, the <code>/th/api/list</code> call is normally used
                    to get the list of available treasure hunts. On the contrary, the <code>/th/test-api/list</code>
                    call allows to request a list with a specified number of treasure hunts, thus allowing to develop
                    Unit Tests that check the underlying communication code, as well as Tests that verify the correct
                    response of the UI that lists the treasure hunts. Finally, the testing API allows for explicitly
                    requesting error messages that would be hard to simulate otherwise.
                </p>
            </div>
        </div>

        <div class="row">
            <div class="col">
                <a name="calls"></a>
                <div style="height: 58px;"></div>
                <h3>Calls</h3>
                <p>
                    The essence of the <a href="/th/guide#calls">Treasure Hunt API</a> is the various function calls
                    available to the clients. This section describes the Testing API calls, which allow similar
                    interactions with the actual API, but in a controlled environment.
                </p>
                <p>
                    In principle, the testing calls use the same conventions as the actual calls, but instead of the
                    <kbd>/th/api/&lt;<i>call</i>&gt;</kbd>, they take the form
                    <kbd>/th/test-api/&lt;<i>call</i>&gt;</kbd>.
                </p>

                <a name="list"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/test-api/list</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Simulates the <code>/th/api/list</code> call.
                            Also see the actual API's call in the <a href="/th/guide#list" target="_blank">guide</a>.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code id="api-list">https://codecyprus.org/th/test-api/list?number-of-ths=2</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/test-api/list?number-of-ths=2" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-target="#api-list">Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has this parameter:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>number-of-ths</code> is an <i>optional</i> parameter specifying the number
                                    of treasure hunts to be returned. If omitted, or an invalid integer or a negative,
                                    then the default value of <code><%=DEFAULT_NUMBER_OF_TREASURE_HUNTS%></code> is returned.
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <a name="start"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/test-api/start</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Simulates the <code>/th/api/start</code> call.
                            Also see the actual API's call in the <a href="/th/guide#start" target="_blank">guide</a>.
                            In this call, the player must use the <code>start</code> call and either not specify
                            anything (in which case a valid message with random values is returned) or specify an
                            error by setting the <code>player</code> parameter, as discussed below.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/test-api/start?player=inactive</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/test-api/start?player=inactive" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/test-api/start?player=inactive" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has one optional parameter:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>player</code> specifies the type of error message to be returned. The
                                    available options are:
                                        <ul>
                                            <li><%=INACTIVE%>: produces the error message '<%=TestStartServlet.ERROR_MESSAGES.get(INACTIVE)%>'</li>
                                            <li><%=EMPTY%>: produces the error message '<%=TestStartServlet.ERROR_MESSAGES.get(EMPTY)%>'</li>
                                            <li><%=PLAYER%>: produces the error message '<%=TestStartServlet.ERROR_MESSAGES.get(PLAYER)%>'</li>
                                            <li><%=APP%>: produces the error message '<%=TestStartServlet.ERROR_MESSAGES.get(APP)%>'</li>
                                            <li><%=UNKNOWN%>: produces the error message '<%=TestStartServlet.ERROR_MESSAGES.get(UNKNOWN)%>'</li>
                                            <li><%=MISSING_PARAMETER%>: produces the error message '<%=TestStartServlet.ERROR_MESSAGES.get(MISSING_PARAMETER)%>'</li>
                                        </ul>
                                    </li>
                                    <li>
                                        If you skip the parameter, then a default correct message is returned, containing random data.
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <a name="question"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/test-api/question</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Simulates the <code>/th/api/question</code> call. Also see the actual API's call in the
                            <a href="/th/guide#question" target="_blank">guide</a>. In this call, the player requests
                            the next question. The returned type can be configured using a number of parameters.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/test-api/question?completed&question-type=MCQ&can-be-skipped&requires-location</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/test-api/question?completed&question-type=MCQ&can-be-skipped&requires-location" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/test-api/question?completed&question-type=MCQ&can-be-skipped&requires-location" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has four optional parameters:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code><%=PARAMETER_COMPLETED%></code> is a boolean parameter specifying whether
                                        the corresponding treasure hunt has already been completed.
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                    <li><code><%=PARAMETER_QUESTION_TYPE%></code> is used to specify the requested type
                                        for the question, i.e. whether it should be <code><%=QuestionType.BOOLEAN%></code>,
                                        <code><%=QuestionType.MCQ%></code>, <code><%=QuestionType.INTEGER%></code>,
                                        <code><%=QuestionType.NUMERIC%></code> or <code><%=QuestionType.TEXT%></code>.
                                        By default the question type is set to <code><%=QuestionType.TEXT%></code>.
                                    </li>
                                    <li><code><%=PARAMETER_CAN_BE_SKIPPED%></code> is a boolean parameter specifying
                                        whether the corresponding question can be skipped or not.
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                    <li><code><%=PARAMETER_REQUIRES_LOCATION%></code> is a boolean parameter specifying
                                        whether the corresponding question is <i>location sensitive</i>.
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <a name="answer"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/test-api/answer</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Simulates the <code>/th/api/answer</code> call. Also see the actual API's call in the
                            <a href="/th/guide#answer" target="_blank">guide</a>. In this call, the player requests
                            the next question. The returned status (e.g. correct or not) can be configured using parameters.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/test-api/answer?correct&completed=false</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/test-api/answer?correct&completed=false" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/test-api/answer?correct&completed=false" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has two parameters:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li>
                                        <code><%=TestAnswerServlet.PARAMETER_CORRECT%></code> is a boolean parameter
                                        specifying whether the answer should be treated as <i>correct</i> or <i>wrong</i>.
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                    <li>
                                        <code><%=TestAnswerServlet.PARAMETER_COMPLETED%></code> is a boolean parameter
                                        specifying whether the corresponding session has already been completed or not.
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <a name="score"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/test-api/score</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Simulates the <code>/th/api/score</code> call. Also see the actual API's call in the
                            <a href="/th/guide#score" target="_blank">guide</a>. In this call, the player requests
                            the score of the player, which can be configured using a parameter.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/test-api/score?score=42</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/test-api/score?score=42" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/test-api/score?score=42" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has two parameters:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li>
                                        <code><%=TestScoreServlet.PARAMETER_SCORE%></code> is used to specify the
                                        numerical value to be returned. If invalid or negative, then a default value of
                                        42 is returned.
                                    </li>
                                    <li>
                                        <code><%=TestScoreServlet.PARAMETER_COMPLETED%></code> is an optional parameter
                                        which when specified indicates that the treasure hunt is completed (i.e. all
                                        questions have been answered).
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                    <li>
                                        <code><%=TestScoreServlet.PARAMETER_FINISHED%></code> is an optional parameter
                                        which when specified indicates that the treasure hunt has finished.
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                    <li>
                                        <code><%=TestScoreServlet.PARAMETER_ERROR%></code> is an optional parameter
                                        which when specified returns an error message instead (i.e. 'Invalid session
                                        id'), ignoring the <code>score</code> value if specified.
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <a name="leaderboard"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/test-api/leaderboard</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Simulates the <code>/th/api/leaderboard</code> call. Also see the actual API's call in the
                            <a href="/th/guide#leaderboard" target="_blank">guide</a>. In this call, the player requests
                            the leaderboard containing all the players and their scores. The number of players to be
                            returned, as well as whether the list is sorted, can be configured using parameters.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/test-api/leaderboard?sorted&size=42</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/test-api/leaderboard?sorted&size=42" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/test-api/leaderboard?sorted&size=42" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has two parameters:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li>
                                        <code><%=TestLeaderboardServlet.PARAMETER_SIZE%></code> specifies the number of
                                        entries in the leaderboard. If not provided, or if an invalid or negative value
                                        is provided, the default value of
                                        <code><%=TestLeaderboardServlet.DEFAULT_SIZE%></code> is used.
                                    </li>
                                    <li><code><%=TestLeaderboardServlet.PARAMETER_SORTED%></code> is an <i>optional</i>
                                        parameter for specifying that the score list is to be sorted from higher to
                                        lower scores.
                                        This value can be specified simply by its presence, i.e. no value must be set,
                                        or using the standard key/value pair where the value is set to <code>true</code>.
                                        By default this is set to <code>false</code> (if not present).
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>

    </div>

    <footer class="footer">
        <div class="container">
            <span class="text-muted">Code Cyprus &mdash; Treasure Hunt API Guide &copy; <%=new SimpleDateFormat("yyyy").format(new Date())%></span>
        </div>
    </footer>

    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js" integrity="sha384-vFJXuSJphROIrBnz7yo7oB41mKfc8JzQZiCq4NCceLEaO4IHwicKwpJf9c9IpFgh" crossorigin="anonymous"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.min.js" integrity="sha384-alpBpkh1PFOepccYVYDB4do5UnbKysX5WZXm3XxPqe5iKTfUKjNkCk9SaVuEZflJ" crossorigin="anonymous"></script>

    <%-- Required for clipboard copy--%>
    <script src="https://cdn.rawgit.com/zenorocha/clipboard.js/v2.0.0/dist/clipboard.min.js"></script>
    <script src="https://d3js.org/d3.v3.min.js" language="JavaScript"></script>

    <script>
        // init clipboard
        new ClipboardJS('.btn'); // initialize clipboard code for buttons of class 'btn'
    </script>

</body>
</html>