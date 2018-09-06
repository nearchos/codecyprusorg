<%@ page import="org.codecyprus.th.model.QuestionType" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %><%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 19-Aug-18
  Time: 9:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <title>Code Cyprus Treasure Hunt: API Guide</title>
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
<body>

    <nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
        <a class="navbar-brand" href="#"><img src="/th/pirate.png" height="25" alt="Code Cyprus - Pirate API logo" title="Code Cyprus - Pirate API logo"> Treasure Hunt API Guide</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="#intro">Introduction</a>
                </li>

                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Overview
                    </a>
                    <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                        <a class="dropdown-item" href="#requests">Requests</a>
                        <a class="dropdown-item" href="#replies">Replies</a>
                        <a class="dropdown-item" href="#errors">Errors</a>
                    </div>
                </li>

                <li class="nav-item">
                    <a class="nav-link" href="#concepts">Concepts</a>
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
                    <a class="nav-link" href="#location"><kbd>/location</kbd></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#skip"><kbd>/skip</kbd></a>
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
        <div class="jumbotron">
            <p class="display-3"><img src="/th/pirate_api.png" alt="Code Cyprus - Pirate API logo" title="Code Cyprus - Pirate API logo" height="120"/> Treasure Hunt: API Guide</p>
            <p class="lead">A Code Cyprus project</p>
            <hr class="my-4">
            <p>
                This is version <kbd>1.0.0</kbd> of the Treasure Hunt API Guide. It is available online at <code>http://www.codecyprus.org/th/guide</code>.
            </p>
        </div>

        <div class="row">
            <div class="col">
                <a name="intro"></a>
                <div style="height: 58px;"></div>
                <h3>Introduction</h3>
                <p>
                    This app provides the server-side functionality for the Treasure Hunt challenge. This challenge is
                    undertaken by students pursuing the <a href="http://computing.uclancyprus.ac.cy" target="_blank">
                    BSc (Hons) Computing degree</a> at <a href="http://uclancyprus.ac.cy" target="_blank">UCLan Cyprus</a>,
                    as part of module CO1111 (The Computing Challenge). This backend is deployed on Google's app-engine.
                </p>
                <p>
                    The concept of the challenge is inspired from the equivalent Four-week challenge, originally
                    created at UCLan in Preston. Given the API description of the service, the students are asked to
                    develop mobile apps either using AppInventor or pure HTML+JavaScript. The main goal is to provide
                    students with an overview of key practical aspects of computing.
                </p>
                <p>
                    This project is open-source, and its <a href="http://github.com/nearchos/codecyprusorg" target="_blank">
                    code is available for cloning from GitHub</a> under the <a href="https://opensource.org/licenses/LGPL-3.0" target="_blank">LGPL-3.0 license</a>.
                    For feedback or questions please contact npaspallis at uclan point ac dot uk.
                </p>
            </div>
        </div>

        <div class="row">
            <div class="col">
                <a name="overview"></a>
                <div style="height: 58px;"></div>
                <h3>Overview of the API</h3>
                <p>
                    An API (<i>Application Programming Interface</i>) is a set of functions, typically available over
                    the Web, which allow third-party developers to integrate with an existing app/functionality. In this
                    particular case, the API allows anyone interested to build a Treasure Hunt app, such as the
                    <a href="https://play.google.com/store/apps/details?id=org.codecyprus.android_client" target="_blank">
                    formal Android App of Code Cyprus</a>, to do so.
                </p>
                <p>
                    Like most Web APIs, this one realizes the client/server paradigm where the client makes <i>requests</i>
                    and the <i>server</i> answers them. For instance, a request is to provide a <i>list</i> of the
                    available Treasure Hunts.
                </p>
                <p>
                    To enable maximum security possible, the whole API requires secure connections and thus any requests
                    applied to <code>http://codecyprus.org/th/api/...</code> are automatically redirected to their
                    secure equivalent <code>https://codecyprus.org/th/api/...</code>. This is in line with best practices
                    <a href="https://support.google.com/webmasters/answer/6073543?hl=en" target="_blank">recommended by
                    the likes of Google etc.</a> who even rank websites higher when they are secured with HTTPS.
                </p>
                <a name="requests"></a>
                <div style="height: 52px;"></div>
                <h4>Requests</h4>
                <p>
                    Requests are generally formed as URLs, such as <code>http://codecyprus.org/th/api/list</code>. In this
                    API, all requests are based on the same server URL <code>http://codecyprus.org</code> and use a path
                    that has a prefix of <code>/th/api/&lt;call&gt;</code> where <code>&lt;call&gt;</code> identifies
                    one of the available calls described below.
                </p>
                <p>
                    Requests can also include parameters. Those are defined as <code>&lt;name&gt;=&lt;value&gt;</code>
                    pairs, such as <code>answer=42</code>. In this example the <i>name</i> of the parameter is
                    <code>answer</code> and its value is <code>42</code>. If multiple parameters need to be specified in
                    the same call, you can join them using the ampersand symbol <code>&amp;</code>. For example you
                    could specify two parameters <code>player</code> and <code>app</code> as
                    <code>player=Homer&app=simpsons-app</code>. Note that parameters are defined right after the
                    call and their beginning is identified with a question mark <code>?</code>. For example, a full call
                    URL is <code>http://codecyprus.org/th/api/start?player=Homer&app=simpsons-android</code>.
                </p>
                <p>
                    Last note that Boolean parameters are sometimes defined as just a <code>name</code> rather than a full
                    <code>&lt;name&gt;=&lt;value&gt;</code> pair. For instance <code>include-finished</code> is a valid
                    parameter and the call <code>https://codecyprus.org/th/api/list?include-finished=true</code> is
                    equivalent to <code>https://codecyprus.org/th/api/list?include-finished</code>.
                </p>
                <a name="replies"></a>
                <div style="height: 52px;"></div>
                <h4>Replies</h4>
                <p>
                    Following a request, the server replies with a proper reply message. Replies are encoded in
                    <a href="http://www.json.org" target="_blank">JSON</a> (<i>JavaScript Object Notation</i>) which is
                    probably the most widely-used encoding format used in Web APIs. if you need a fresh-up on JSON you
                    can follow any of the many online tutorials, such as
                    <a href="https://www.w3schools.com/js/js_json_intro.asp" target="_blank"> this one from W3Schools</a>.
                </p>
                <p>
                    Each API call has its own specific reply, encoding relevant information as needed. Data included in
                    specific call replies are discussed in detail in the <a href="#calls">calls</a> section.
                    Nevertheless, one thing common in all replies is the <code>status</code> property which gets the
                    value <code>OK</code> when the call was successful or <code>ERROR</code> if a problem occurred, such
                    as a required parameter that is missing.
                </p>
                <a name="errors"></a>
                <div style="height: 52px;"></div>
                <h4>Errors</h4>
                <p>
                    An error can occur for many reasons. A proper client app must anticipate errors and either overcome
                    them (e.g. retry if there was a connection error) or inform the user accordingly (e.g. when trying
                    to use a player name that is already in use).
                </p>
                <p>
                    Errors can occur if you make a mistake in forming the URL (e.g. a
                    <a href="https://en.wikipedia.org/wiki/HTTP_404" target="_blank">404 resource not found error</a>
                    if you mistype the server URL) or if there is an error outside your control (e.g. a
                    <a href="https://en.wikipedia.org/wiki/HTTP_500" target="_blank">500 internal server error</a>).
                </p>
                <p>
                    Nevertheless, errors might occur even when the server responds with a success code (i.e.
                    <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#2xx_Success" target="_blank">
                    200 OK</a>). In this case, the error will be identified in the reply message, with a <code>status</code>
                    property marked as <code>ERROR</code> and an array of one or more error messages named
                    <code>errorMessages</code>.
                </p>
            </div>
        </div>

        <div class="row">
            <div class="col">
                <a name="concepts"></a>
                <div style="height: 58px;"></div>
                <h3>Concepts</h3>
                <p>
                    The Treasure Hunt is built around the concepts of <code>treasure hunt</code>, <code>session</code>,
                    and <code>question</code>.
                </p>
                <p>
                    The <code>treasure hunt</code> is the main concept describing a treasure hunt game that includes
                    <i>questions</i>, and <i>players</i>. Normally, every treasure hunt has a starting and an ending
                    time. The list of available treasure hunts can be accessed using the <a href="#list">/th/api/list</a>
                    call.
                </p>
                <p>
                    The <code>session</code> is an instance of an active player. Each session is associated with one
                    treasure hunt and includes information like the player name, the current question, etc. A session
                    is created when the player uses the <a href="#start">/th/api/start</a> call.
                </p>
                <p>
                    A <code>question</code> includes the information required to describe the question to the user
                    and includes the question text itself, the expected type of the answer (e.g. Boolean, Integer,
                    Multiple Choice Question, etc.) and whether it is mandatory or can be skipped. A player can get
                    the current question using the <a href="#question">/th/api/question</a> call.
                </p>
            </div>
        </div>

        <div class="row">
            <div class="col">
                <a name="calls"></a>
                <div style="height: 58px;"></div>
                <h3>Calls</h3>
                <p>
                    The essence of the API is the various function calls available to the clients. This section
                    describes the available calls, their semantics, and the expected returned data.
                </p>

                <a name="list"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/api/list</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            The starting point of any treasure hunt challenge is to list the available treasure hunts so the
                            player can pick the one they want to compete in. This is enabled using the
                            <code>/th/api/list</code> call.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code id="api-list">https://codecyprus.org/th/api/list</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/api/list" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-target="#api-list">Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has these parameters:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>include-finished</code> is an <i>optional</i> parameter specifying that the
                                    reply must include all treasure hunts, including finished ones. This is usually not needed.
                                    A sample request is <code>https://codecyprus.org/th/api/list?include-finished</code></li>
                                </ul>
                            </div>
                        </div>
                        <p>
                            The output includes the <code>status</code> and the array of available
                            <code>treasureHunts</code>.
                        </p>
                        <div class="card card-body">
                            <p>
                                <a class="btn btn-primary" data-toggle="collapse" href="#collapse-call-list" aria-expanded="false" aria-controls="collapse-call-list">Show sample output</a>
                            </p>
                            <div class="collapse" id="collapse-call-list">
<pre id="call-list-result-pre"></pre>
                            </div>
                        </div>
                    </div>
                </div>

                <a name="start"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/api/start</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            To join a treasure hunt, the player must use the <code>start</code> call and specify their
                            name, app id and requested treasure hunt id.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/api/start?player=Homer&app=simpsons-app&treasure-hunt-id=ag9nfmNv...AvKGCCgw</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/api/start?player=Homer&app=simpsons-app&treasure-hunt-id=ag9nfmNvZGVjeXBydXNvcmdyGQsSDFRyZWFzdXJlSHVudBiAgICAvKGCCgw" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/api/start?player=Homer&app=simpsons-app&treasure-hunt-id=ag9nfmNvZGVjeXBydXNvcmdyGQsSDFRyZWFzdXJlSHVudBiAgICAvKGCCgw" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has these parameters:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>player</code> is a <i>mandatory</i> parameter specifying the requested
                                        player name or nickname. This is required to be unique, so if the specified
                                        player is already in use, an appropriate error message is returned.</li>
                                    <li><code>app</code> is also a <i>mandatory</i> parameter specifying the name of
                                        the app used to play the treasure hunt. This is a required parameter, so if it
                                        is not specified, an appropriate error message is returned.</li>
                                    <li><code>treasure-hunt-id</code> is a <i>mandatory</i> parameter specifying the id
                                        of the treasure hunt to be launched. The id is normally picked from the result
                                        of the <a href="#list">list</a> call. This is a required parameter and it must
                                        be a valid id, i.e. one that corresponds to an existing and available treasure
                                        hunt. If not, an appropriate error message is returned.</li>
                                </ul>
                            </div>
                        </div>
                        <p>
                            The output includes the <code>status</code> the total <code>numOfQuestions</code> and the
                            <code>session</code> id which is required for subsequent calls, such as to get the current
                            <a href="#question">question</a>.
                        </p>
                        <div class="card card-body">
                            <p>
                                <a class="btn btn-primary" data-toggle="collapse" href="#collapse-call-start" aria-expanded="false" aria-controls="collapse-call-start">Show sample output</a>
                            </p>
                            <div class="collapse" id="collapse-call-start">

                                <nav class="nav nav-tabs" role="tablist">
                                    <a class="nav-item nav-link active" id="nav-call-start-result" data-toggle="tab" href="#call-start-result" role="tab" aria-controls="nav-start-result" aria-selected="true">Result</a>
                                    <a class="nav-item nav-link" id="nav-call-start-error-player-in-use" data-toggle="tab" href="#call-start-error-player-in-use" role="tab" aria-controls="nav-start-error-player-in-use" aria-selected="false">Error (player name in use)</a>
                                    <a class="nav-item nav-link" id="nav-call-start-error-missing-parameter" data-toggle="tab" href="#call-start-error-missing-parameter" role="tab" aria-controls="nav-call-start-error-missing-parameter" aria-selected="false">Error (missing parameter)</a>
                                    <a class="nav-item nav-link" id="nav-call-start-error-unknown-th" data-toggle="tab" href="#call-start-error-unknown-th" role="tab" aria-controls="nav-call-start-error-unknown-th" aria-selected="false">Error (unknown treasure hunt id)</a>
                                </nav>
                                <div class="tab-content" id="nav-tabContent">
                                    <div class="tab-pane fade show active" id="call-start-result" role="tabpanel" aria-labelledby="nav-call-start-result">
<pre id="call-start-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-start-error-player-in-use" role="tabpanel" aria-labelledby="nav-call-start-error-player-in-use">
<pre id="call-start-error-player-in-use-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-start-error-missing-parameter" role="tabpanel" aria-labelledby="nav-call-start-error-missing-parameter">
<pre id="call-start-error-missing-parameter-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-start-error-unknown-th" role="tabpanel" aria-labelledby="nav-call-start-error-unknown-th">
<pre id="call-start-error-unknown-th-pre"></pre>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

                <a name="question"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/api/question</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Once you join a treasure hunt you can start looking up questions. To view a question, you
                            can use the <code>/th/api/question</code> call. This gives you information about the actual
                            question (i.e. its text) as well as the expected answer type.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/api/question?session=ag9nfmNv...oMa0gQoM</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/api/question?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/api/question?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has one parameter:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>session</code> is a <i>mandatory</i> parameter specifying the id of the
                                        session which corresponds to this player.</li>
                                </ul>
                            </div>
                        </div>
                        <p>
                            The output includes the <code>status</code> and some properties of the <code>question</code>.
                            For instance the <code>completed</code> property specifies if the user has already completed
                            this treasure hunt (i.e. has answered all questions already). The <code>questionText</code>
                            contains the text-based question, whcih can also be specified in simple HTML. The
                            <code>questionType</code> specifies the expected type of the answer. The possible answer
                            types are:
                        </p>
                        <ul>
                            <li><code><%=QuestionType.BOOLEAN.name()%></code> can be either Boolean value true/false</li>
                            <li><code><%=QuestionType.INTEGER.name()%></code> can be any valid integer like -1, 0, 1, 2, etc.</li>
                            <li><code><%=QuestionType.NUMERIC.name()%></code> can be any valid numeric value like -1.2, 0.2, 1.234, etc.</li>
                            <li><code><%=QuestionType.MCQ.name()%></code> can be any of four possible multiple-choice answers, i.e. A, B, C, D</li>
                            <li><code><%=QuestionType.TEXT.name()%></code> can be any general text, normally a single word</li>
                        </ul>
                        <p>
                            The <code>canBeSkipped</code> is a Boolean that specifies whether this particular question
                            can be skipped or must be answered. The <code>requires-location</code> indicates whether
                            this is a location sensitive question where the player must be at a specific location for
                            their answer to be checked. The <code>numOfQuestions</code> specifies the total number of
                            questions in the treasure hunt, and finally the <code>currentQuestionIndex</code> defines
                            the zero-based index of the current question (the first one is 0, and the last one is
                            <code>numOfQuestions</code>-1).
                        </p>
                        <div class="card card-body">
                            <p>
                                <a class="btn btn-primary" data-toggle="collapse" href="#collapse-call-question" aria-expanded="false" aria-controls="collapse-call-question">Show sample output</a>
                            </p>
                            <div class="collapse" id="collapse-call-question">

                                <nav class="nav nav-tabs">
                                    <a class="nav-item nav-link active" id="nav-call-question-result" data-toggle="tab" href="#call-question-result" role="tab" aria-controls="nav-call-question-result" aria-selected="true">Result</a>
                                    <a class="nav-item nav-link" id="nav-call-question-error-missing-parameter" data-toggle="tab" href="#call-question-error-missing-parameter" role="tab" aria-controls="nav-call-question-error-missing-parameter" aria-selected="false">Error (missing parameter)</a>
                                    <a class="nav-item nav-link" id="nav-call-question-error-invalid-th" data-toggle="tab" href="#call-question-error-invalid-th" role="tab" aria-controls="nav-call-question-error-invalid-th" aria-selected="false">Error (invalid treasure hunt id)</a>
                                </nav>
                                <div class="tab-content">
                                    <div class="tab-pane fade show active" id="call-question-result" role="tabpanel" aria-labelledby="nav-call-question-result">
                                        <pre id="call-question-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-question-error-missing-parameter" role="tabpanel" aria-labelledby="nav-call-question-error-missing-parameter">
                                        <pre id="call-question-error-missing-parameter-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-question-error-invalid-th" role="tabpanel" aria-labelledby="nav-call-question-error-invalid-th">
                                        <pre id="call-question-error-invalid-th-pre"></pre>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

                <a name="answer"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/api/answer</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            To answer the current question you can use the <code>/th/api/answer</code> call. This allows
                            you to specify the <code>answer</code> for the given <code>session</code>.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/api/answer?session=ag9nfmNv...oMa0gQoM&answer=42</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/api/answer?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM&answer=42" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/api/answer?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM&answer=42" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has two parameters:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>session</code> is a <i>mandatory</i> parameter specifying the id of the
                                        session which corresponds to this player.</li>
                                    <li><code>answer</code> is a <i>mandatory</i> parameter for specifying the tries
                                        answer. The answer is provided in text form but must math the question type as
                                        indicated in the reply of the <a href="#question">question</a> call.</li>
                                </ul>
                            </div>
                        </div>
                        <p>
                            The output includes the <code>status</code> and some properties such as whether the provided
                            answer was <code>correct</code>, whether the session is no <code>completed</code> (meaning
                            there are no more unanswered questions), an optional text-based <code>message</code> and
                            the <code>scoreAdjustment</code> as an integer that indicates how the score has changed
                            (i.e. points gained or subtracted from the player's score).
                        </p>
                        <div class="card card-body">
                            <p>
                                <a class="btn btn-primary" data-toggle="collapse" href="#collapse-call-answer" aria-expanded="false" aria-controls="collapse-call-answer">Show sample output</a>
                            </p>
                            <div class="collapse" id="collapse-call-answer">

                                <nav class="nav nav-tabs" role="tablist">
                                    <a class="nav-item nav-link active" id="nav-call-answer-correct-result" data-toggle="tab" href="#call-answer-correct-result" role="tab" aria-controls="nav-call-answer-correct-result" aria-selected="true">Result OK (with correct answer)</a>
                                    <a class="nav-item nav-link" id="nav-call-answer-wrong-result" data-toggle="tab" href="#call-answer-wrong-result" role="tab" aria-controls="nav-call-answer-wrong-result" aria-selected="false">Result OK (with wrong answer)</a>
                                    <a class="nav-item nav-link" id="nav-call-answer-error-run-out-of-time" data-toggle="tab" href="#call-answer-error-run-out-of-time" role="tab" aria-controls="nav-call-answer-error-run-out-of-time" aria-selected="false">Error (treasure hunt has ended)</a>
                                </nav>
                                <div class="tab-content">
                                    <div class="tab-pane fade show active" id="call-answer-correct-result" role="tabpanel" aria-labelledby="nav-call-answer-correct-result">
                                        <pre id="call-answer-correct-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-answer-wrong-result" role="tabpanel" aria-labelledby="nav-call-answer-wrong-result">
                                        <pre id="call-answer-wrong-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-answer-error-run-out-of-time" role="tabpanel" aria-labelledby="nav-call-answer-error-run-out-of-time">
                                        <pre id="call-answer-error-run-out-of-time-pre"></pre>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

                <a name="location"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/api/location</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Some of the questions require that the player is at a specific location to be able to answer.
                            The <code>/th/api/location</code> call allows you to specify the location in terms of
                            <code>latitude</code> and <code>longitude</code> for the given <code>session</code>.
                        </p>
                        <p>
                            The <code>/th/api/location</code> call is used periodically to update the server of the
                            player's current location, and also before answering a location-sensitive
                            <a href="#concepts">question</a> as indicated by the <code>requires-location</code>
                            property of the <a href="#question">/question</a> call. Normally, you can not call
                            <code>/th/api/location</code> too often. Instead allow at least 30 seconds between calls.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/api/location?session=ag9nfmNv...oMa0gQoM&latitude=34.683646&longitude=33.055391</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/api/location?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM&latitude=34.683646&longitude=33.055391" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/api/location?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM&latitude=34.683646&longitude=33.055391" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has three parameters:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>session</code> is a <i>mandatory</i> parameter specifying the id of the
                                        session which corresponds to this player.</li>
                                    <li><code>latitude</code> is a <i>mandatory</i> parameter for specifying the
                                        <a href="https://en.wikipedia.org/wiki/Latitude" target="_blank">latitude</a>
                                        of the current location.</li>
                                    <li><code>longitude</code> is a <i>mandatory</i> parameter for specifying the
                                        <a href="https://en.wikipedia.org/wiki/Longitude" target="_blank">longitude</a>
                                        of the current location.</li>
                                </ul>
                            </div>
                        </div>
                        <p>
                            The output includes the <code>status</code> and a text-based <code>message</code> explaining
                            whether the call was recorded or not.
                        </p>
                        <div class="card card-body">
                            <p>
                                <a class="btn btn-primary" data-toggle="collapse" href="#collapse-call-location" aria-expanded="false" aria-controls="collapse-call-location">Show sample output</a>
                            </p>
                            <div class="collapse" id="collapse-call-location">

                                <nav class="nav nav-tabs">
                                    <a class="nav-item nav-link active" id="nav-call-location-correct-result" data-toggle="tab" href="#call-location-correct-result" role="tab" aria-controls="nav-call-location-correct-result" aria-selected="true">Result OK (with recording successful)</a>
                                    <a class="nav-item nav-link" id="nav-call-location-wrong-result" data-toggle="tab" href="#call-location-wrong-result" role="tab" aria-controls="nav-call-location-wrong-result" aria-selected="false">Result OK (skipped recording)</a>
                                    <a class="nav-item nav-link" id="nav-call-location-missing-parameters" data-toggle="tab" href="#call-location-missing-parameters" role="tab" aria-controls="nav-call-location-missing-parameters" aria-selected="false">Error (treasure hunt has ended)</a>
                                </nav>
                                <div class="tab-content">
                                    <div class="tab-pane fade show active" id="call-location-correct-result" role="tabpanel" aria-labelledby="nav-call-location-correct-result">
                                        <pre id="call-location-correct-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-location-wrong-result" role="tabpanel" aria-labelledby="nav-call-location-wrong-result">
                                        <pre id="call-location-wrong-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-location-missing-parameters" role="tabpanel" aria-labelledby="nav-call-location-missing-parameters">
                                        <pre id="call-location-missing-parameters-pre"></pre>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

                <a name="skip"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/api/skip</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Some of the questions can be skipped, usually with a penalty to the score. The
                            <code>/th/api/skip</code> call allows you to skip a <a href="#question">question</a>
                            if the player chooses to do so.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/api/skip?session=ag9nfmNv...oMa0gQoM</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/api/skip?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/api/skip?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has one parameter:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>session</code> is a <i>mandatory</i> parameter specifying the id of the
                                        selected session.</li>
                                </ul>
                            </div>
                        </div>
                        <p>
                            The output includes the <code>status</code>, a Boolean indication of whether the treasure
                            hunt is <code>completed</code> after skipping, a text-based <code>message</code> and the
                            <code>scoreAdjustment</code> which is normally a negative integer, e.g. -5.
                        </p>
                        <div class="card card-body">
                            <p>
                                <a class="btn btn-primary" data-toggle="collapse" href="#collapse-call-skip" aria-expanded="false" aria-controls="collapse-call-skip">Show sample output</a>
                            </p>
                            <div class="collapse" id="collapse-call-skip">

                                <nav class="nav nav-tabs">
                                    <a class="nav-item nav-link active" id="nav-call-skip-result" data-toggle="tab" href="#call-skip-result" role="tab" aria-controls="nav-call-skip-result" aria-selected="true">Result OK</a>
                                    <a class="nav-item nav-link" id="nav-call-skip-error-cannot-be-skipped" data-toggle="tab" href="#call-skip-error-cannot-be-skipped" role="tab" aria-controls="nav-call-skip-error-cannot-be-skipped" aria-selected="false">Result ERROR (cannot skip question)</a>
                                </nav>
                                <div class="tab-content">
                                    <div class="tab-pane fade show active" id="call-skip-result" role="tabpanel" aria-labelledby="nav-call-skip-result">
                                        <pre id="call-skip-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-skip-error-cannot-be-skipped" role="tabpanel" aria-labelledby="nav-call-skip-error-cannot-be-skipped">
                                        <pre id="call-skip-error-cannot-be-skipped-pre"></pre>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

                <a name="score"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/api/score</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            The <code>/th/api/score</code> call is used to access the current score of this
                            <a href="#concepts">session</a>.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/api/score?session=ag9nfmNv...oMa0gQoM</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/api/score?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/api/score?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has one parameter:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>session</code> is a <i>mandatory</i> parameter specifying the id of the
                                        selected session.</li>
                                </ul>
                            </div>
                        </div>
                        <p>
                            The output includes the <code>status</code>, a Boolean indication of whether the treasure
                            hunt is <code>completed</code>, i.e. there are no more unanswered questions, and whether
                            the treasure hunt is <code>finished</code>, i.e. it has ended time-wise. Last, a
                            <code>score</code> property gives the score as an integer, e.g. 23.
                        </p>
                        <div class="card card-body">
                            <p>
                                <a class="btn btn-primary" data-toggle="collapse" href="#collapse-call-score" aria-expanded="false" aria-controls="collapse-call-score">Show sample output</a>
                            </p>
                            <div class="collapse" id="collapse-call-score">

                                <nav class="nav nav-tabs">
                                    <a class="nav-item nav-link active" id="nav-call-score-result" data-toggle="tab" href="#call-score-result" role="tab" aria-controls="nav-call-score-result" aria-selected="true">Result OK</a>
                                    <a class="nav-item nav-link" id="nav-call-score-error-unknown-session" data-toggle="tab" href="#call-score-error-unknown-session" role="tab" aria-controls="nav-call-score-error-unknown-session" aria-selected="false">Result ERROR (unknown session id)</a>
                                </nav>
                                <div class="tab-content">
                                    <div class="tab-pane fade show active" id="call-score-result" role="tabpanel" aria-labelledby="nav-call-score-result">
                                        <pre id="call-score-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-score-error-unknown-session" role="tabpanel" aria-labelledby="nav-call-score-error-unknown-session">
                                        <pre id="call-score-error-unknown-session-pre"></pre>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

                <a name="leaderboard"></a>
                <div style="height: 58px;"></div>
                <h3><kbd>/th/api/leaderboard</kbd></h3>
                <div class="card my-4">
                    <div class="card-body">
                        <p>
                            Naturally multiple players can be competing in a treasure hunt. To access the current
                            leaderboard us the <code>/th/api/leaderboard</code> call and specify either the current
                            player <code>session</code> or the selected <code>treasure-hunt-id</code>. Optionally,
                            also use the <code>sorted</code> flag to indicate that you want the list of scores to be
                            sorted from higher to smaller score.
                        </p>
                        <p>
                            The API call is as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <code>https://codecyprus.org/th/api/leaderboard?session=ag9nfmNv...oMa0gQoM&sorted</code>
                                <a class="btn btn-primary" href="https://codecyprus.org/th/api/leaderboard?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM&sorted" target="_blank">Try it</a>
                                <a class="btn btn-primary" data-clipboard-text="https://codecyprus.org/th/api/leaderboard?session=ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM&sorted" data-clipboard->Copy</a>
                            </div>
                        </div>
                        <p>
                            The call has three possible parameters. You can only use one of <code>session</code> and
                            <code>treasure-hunt-id</code>. If you specify both, the latter is ignored. The parameters
                            are as follows:
                        </p>
                        <div class="card card-body">
                            <div>
                                <ul>
                                    <li><code>session</code> is an <i>mandatory</i> parameter specifying the id of the
                                        session which corresponds to this player. Not to be used at the same time with
                                    <code>treasure-hunt-id</code> described next.</li>
                                    <li><code>treasure-hunt-id</code> is a <i>mandatory</i> parameter for specifying the
                                        selected treasure hunt. Not to be used at the same time with
                                        <code>session</code> described previously.</li>
                                    <li><code>sorted</code> is an <i>optional</i> parameter for specifying that the
                                        score list is to be sorted from higher to lower scores.</li>
                                </ul>
                            </div>
                        </div>
                        <p>
                            The output includes the <code>status</code>, the <code>numOfPlayers</code>, a Boolean
                            value indicating whether the list is <code>sorted</code> and the <code>leaderboard</code>.
                            The latter consists of a JSON array containing <code>numOfPlayers</code> entries, where
                            each entry has a <code>player</code> name, a <code>score</code> and a
                            <code>completionTime</code>. The latter is a timestamp of when the player answered the last
                            question, expressed in <a href="https://en.wikipedia.org/wiki/Unix_time" target="_blank">
                            Unix epoch in milliseconds</a>. If the player has not finished yet, it is set to zero.
                        </p>
                        <p>
                            Players with higher <code>score</code> are ranked higher irrespective of
                            <code>completionTime</code>. When players have the same <code>score</code> then the player
                            with the smallest <code>completionTime</code> is ranked before (as the player answered the
                            last question earlier). Players with a <code>completionTime</code> of zero ar ranked after
                            other players with the same <code>score</code>.
                        </p>
                        <div class="card card-body">
                            <p>
                                <a class="btn btn-primary" data-toggle="collapse" href="#collapse-call-leaderboard" aria-expanded="false" aria-controls="collapse-call-leaderboard">Show sample output</a>
                            </p>
                            <div class="collapse" id="collapse-call-leaderboard">

                                <nav class="nav nav-tabs">
                                    <a class="nav-item nav-link active" id="nav-call-leaderboard-sorted-result" data-toggle="tab" href="#call-leaderboard-sorted-result" role="tab" aria-controls="nav-call-leaderboard-sorted-result" aria-selected="true">Result OK (sorted)</a>
                                    <a class="nav-item nav-link" id="nav-call-leaderboard-unsorted-result" data-toggle="tab" href="#call-leaderboard-unsorted-result" role="tab" aria-controls="nav-call-leaderboard-unsorted-result" aria-selected="true">Result OK (unsorted)</a>
                                    <a class="nav-item nav-link" id="nav-call-leaderboard-unknown-session" data-toggle="tab" href="#call-leaderboard-unknown-session" role="tab" aria-controls="nav-call-leaderboard-unknown-session" aria-selected="false">Error (unknown session)</a>
                                </nav>
                                <div class="tab-content">
                                    <div class="tab-pane fade show active" id="call-leaderboard-sorted-result" role="tabpanel" aria-labelledby="nav-call-leaderboard-sorted-result">
                                        <pre id="call-leaderboard-sorted-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade show active" id="call-leaderboard-unsorted-result" role="tabpanel" aria-labelledby="nav-call-leaderboard-unsorted-result">
                                        <pre id="call-leaderboard-unsorted-result-pre"></pre>
                                    </div>
                                    <div class="tab-pane fade" id="call-leaderboard-unknown-session" role="tabpanel" aria-labelledby="nav-call-leaderboard-unknown-session">
                                        <pre id="call-leaderboard-unknown-session-pre"></pre>
                                    </div>
                                </div>

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

    <%--Custom JavaScript containing sample replies--%>
    <script src="/th/sample-results.js" crossorigin="anonymous"></script>

    <script>
        // init clipboard
        new ClipboardJS('.btn'); // initialize clipboard code for buttons of class 'btn'

        // init sample results
        document.getElementById("call-list-result-pre").innerHTML = JSON.stringify(callListResult, undefined, 2);

        document.getElementById("call-start-result-pre").innerHTML = JSON.stringify(callStartResult, undefined, 2);
        document.getElementById("call-start-error-player-in-use-pre").innerHTML = JSON.stringify(callStartErrorPlayer, undefined, 2);
        document.getElementById("call-start-error-missing-parameter-pre").innerHTML = JSON.stringify(callStartErrorMissingParameter, undefined, 2);
        document.getElementById("call-start-error-unknown-th-pre").innerHTML = JSON.stringify(callStartErrorUnknownTH, undefined, 2);

        document.getElementById("call-question-result-pre").innerHTML = JSON.stringify(callQuestionResult, undefined, 2);
        document.getElementById("call-question-error-missing-parameter-pre").innerHTML = JSON.stringify(callQuestionErrorMissingParameter, undefined, 2);
        document.getElementById("call-question-error-invalid-th-pre").innerHTML = JSON.stringify(callQuestionErrorUnknownTH, undefined, 2);

        document.getElementById("call-answer-correct-result-pre").innerHTML = JSON.stringify(callAnswerResultCorrectAnswer, undefined, 2);
        document.getElementById("call-answer-wrong-result-pre").innerHTML = JSON.stringify(callAnswerResultWrongAnswer, undefined, 2);
        document.getElementById("call-answer-error-run-out-of-time-pre").innerHTML = JSON.stringify(callAnswerErrorRunOutOfTime, undefined, 2);

        document.getElementById("call-location-correct-result-pre").innerHTML = JSON.stringify(callLocationResultCorrectAnswer, undefined, 2);
        document.getElementById("call-location-wrong-result-pre").innerHTML = JSON.stringify(callLocationResultWrongAnswer, undefined, 2);
        document.getElementById("call-location-missing-parameters-pre").innerHTML = JSON.stringify(callLocationMissingParameters, undefined, 2);

        document.getElementById("call-skip-result-pre").innerHTML = JSON.stringify(callSkipResult, undefined, 2);
        document.getElementById("call-skip-error-cannot-be-skipped-pre").innerHTML = JSON.stringify(callSkipErrorCannotBeSkipped, undefined, 2);

        document.getElementById("call-score-result-pre").innerHTML = JSON.stringify(callScoreResult, undefined, 2);
        document.getElementById("call-score-error-unknown-session-pre").innerHTML = JSON.stringify(callScoreErrorUnknownSession, undefined, 2);

        document.getElementById("call-leaderboard-sorted-result-pre").innerHTML = JSON.stringify(callLeaderboardSortedResult, undefined, 2);
        document.getElementById("call-leaderboard-unsorted-result-pre").innerHTML = JSON.stringify(callLeaderboardUnsortedResult, undefined, 2);
        document.getElementById("call-leaderboard-unknown-session").innerHTML = JSON.stringify(callLeaderboardErrorUnknownSession, undefined, 2);

    </script>

</body>
</html>