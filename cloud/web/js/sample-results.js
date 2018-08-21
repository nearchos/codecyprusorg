var callLeaderboardSortedResult = {
    "status": "OK",
    "num-of-players": 3,
    "sorted": false,
    "leaderboard": [
        {
            "player": "Lisa",
            "score": 40,
            "completion-time": 1534834032000
        },
        {
            "player": "Marge",
            "score": 7,
            "completion-time": 1534834037000
        },
        {
            "player": "Apu",
            "score": 7,
            "completion-time": 1534834087000
        },
        {
            "player": "Bart",
            "score": 7,
            "completion-time": 0
        },
        {
            "player": "Homer",
            "score": 0,
            "completion-time": 0
        }
    ]
};

var callLeaderboardUnsortedResult = {
    "status": "OK",
    "num-of-players": 3,
    "sorted": false,
    "leaderboard": [
        {
            "player": "Marge",
            "score": 7,
            "completion-time": 1534834037000
        },
        {
            "player": "Bart",
            "score": 7,
            "completion-time": 0
        },
        {
            "player": "Lisa",
            "score": 40,
            "completion-time": 1534834032000
        },
        {
            "player": "Homer",
            "score": 0,
            "completion-time": 0
        },
        {
            "player": "Apu",
            "score": 7,
            "completion-time": 1534834087000
        }
    ]
};

callLeaderboardErrorUnknownSession = {
    "status": "ERROR",
    "error-messages": [
        "Unknown session. The specified session ID could not be found."
    ]
};

var callScoreResult = {
    "status": "OK",
    "completed": false,
    "finished": false,
    "score": 12
};

var callScoreErrorUnknownSession = {
    "status": "ERROR",
    "error-messages": [
        "Unknown session. The specified session ID could not be found."
    ]
};

var callSkipResult = {
    "status": "OK",
    "completed": false,
    "message": "Skipped.",
    "score-adjustment": -5
};

var callSkipErrorCannotBeSkipped = {
    "status": "ERROR",
    "error-messages": [
        "Cannot skip. This questions is defined as one that cannot be skipped."
    ]
};

var callLocationResultCorrectAnswer = {
    "status": "OK",
    "message": "Added location (34.683646, 33.055391)"
};

var callLocationResultWrongAnswer = {
    "status": "OK",
    "message": "Ignored update as the previous update was less than 30 seconds earlier."
};

var callLocationMissingParameters = {
    "status": "ERROR",
    "error-messages": [
        "Missing or empty parameter: session",
        "Missing or empty parameter: longitude",
        "Invalid non-numeric parameter: latitude",
        "Invalid non-numeric parameter: longitude"
    ]
};

var callAnswerResultCorrectAnswer = {
    "status": "OK",
    "correct": true,
    "completed": false,
    "message": "Well done.",
    "score-adjustment": 10
};

var callAnswerResultWrongAnswer = {
    "status": "OK",
    "correct": false,
    "completed": false,
    "message": "Wrong answer: 41",
    "score-adjustment": -3
};

var callAnswerErrorRunOutOfTime = {
    "status": "ERROR",
    "error-messages": [
        "Finished session. The specified session has run out of time."
    ]
};

var callQuestionResult = {
    "status": "OK",
    "completed": false,
    "question-text": "What is the answer to life?",
    "question-type": "INTEGER",
    "can-be-skipped": true,
    "num-of-questions": 4,
    "current-question-index": 0
};

var callQuestionErrorMissingParameter = {
    "status": "ERROR",
    "error-messages": [
        "Missing or empty parameter: session"
    ]
};

var callQuestionErrorUnknownTH = {
    "status": "ERROR",
    "error-messages": [
        "Unknown session. The specified session ID could not be found."
    ]
};

var callListResult = {
    "status": "OK",
    "treasure-hunts": [
        {
            "uuid": "ag9nfmNvZGVjeXBydXNvcmdyGQsSDFRyZWFzdXJlSHVudBiAgICgpJWCCQw",
            "name": "An expired treasure hunt",
            "description": "A treasure hunt explicitly set to have ended, for demo purposes.",
            "ownerEmail": "nearchos@gmail.com",
            "visibility": "PUBLIC",
            "startsOn": 1534654800000,
            "endsOn": 1534741200000,
            "maxDuration": 0,
            "shuffled": false,
            "requiresAuthentication": false,
            "emailResults": false
        },
        {
            "uuid": "ag9nfmNvZGVjeXBydXNvcmdyGQsSDFRyZWFzdXJlSHVudBiAgICAvKGCCgw",
            "name": "Sample treasure hunt",
            "description": "A treasure hunt with sample questions for testing.",
            "ownerEmail": "nearchos@gmail.com",
            "visibility": "PUBLIC",
            "startsOn": 1534741200000,
            "endsOn": 2166498000000,
            "maxDuration": 300000,
            "shuffled": true,
            "requiresAuthentication": false,
            "emailResults": false
        }
    ]
};

var callStartResult = {
    "status": "OK",
    "session": "ag9nfmNvZGVjeXBydXNvcmdyFAsSB1Nlc3Npb24YgICAoMa0gQoM",
    "num-of-questions": 4
};

var callStartErrorPlayer = {
    "status": "ERROR",
    "error-messages": [
        "The specified playerName: Homer, is already in use (try a different one)."
    ]
};

var callStartErrorMissingParameter = {
    "status": "ERROR",
    "error-messages": [
        "Missing or empty parameter: app"
    ]
};

var callStartErrorUnknownTH = {
    "status": "ERROR",
    "error-messages": [
        "Could not find a treasure hunt for the specified id: ag9nfmNvZGVjeXBydXNvcmdyGQsSDFRyZWFzdXJlSHVudBiAgICAvKGCCg"
    ]
};

