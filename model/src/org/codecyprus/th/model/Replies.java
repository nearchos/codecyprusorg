package org.codecyprus.th.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class Replies {

    static public class ListReply implements Serializable {
        private String status = "OK";
        @SerializedName("treasureHunts")
        private final Vector<TreasureHunt> selectedTreasureHunts;

        public ListReply(Vector<TreasureHunt> selectedTreasureHunts) {
            this.selectedTreasureHunts = selectedTreasureHunts;
        }

        public String getStatus() {
            return status;
        }

        public Vector<TreasureHunt> getSelectedTreasureHunts() {
            return selectedTreasureHunts;
        }
    }

    static public class StartReply implements Serializable {

        private String status = "OK";
        @SerializedName("session")
        private String sessionId;
        @SerializedName("numOfQuestions")
        private int numOfQuestions;

        public StartReply(String sessionId, int numOfQuestions) {
            this.sessionId = sessionId;
            this.numOfQuestions = numOfQuestions;
        }
    }

    static public class QuestionReply implements Serializable {

        private String status = "OK";

        private boolean completed;

        @SerializedName("questionText")
        private String questionText;

        @SerializedName("questionType")
        private QuestionType questionType;

        @SerializedName("canBeSkipped")
        private boolean canBeSkipped;

        @SerializedName("requiresLocation")
        private boolean requiresLocation;

        @SerializedName("numOfQuestions")
        private int numOfQuestions;

        @SerializedName("currentQuestionIndex")
        private int currentQuestionIndex;

        public QuestionReply(boolean completed, String questionText, QuestionType questionType, boolean canBeSkipped, boolean requiresLocation, int numOfQuestions, int currentQuestionIndex) {
            this.completed = completed;
            this.questionText = questionText;
            this.questionType = questionType;
            this.canBeSkipped = canBeSkipped;
            this.requiresLocation = requiresLocation;
            this.numOfQuestions = numOfQuestions;
            this.currentQuestionIndex = currentQuestionIndex;
        }
    }

    static public class AnswerReply implements Serializable {

        private String status = "OK";
        private boolean correct;
        private boolean completed;
        private String message;
        @SerializedName("scoreAdjustment")
        private int scoreAdjustment;

        public AnswerReply(boolean correct, boolean completed, String message, int scoreAdjustment) {
            this.correct = correct;
            this.completed = completed;
            this.message = message;
            this.scoreAdjustment = scoreAdjustment;
        }
    }

    static public class LocationReply implements Serializable {

        private String status = "OK";
        private String message;

        public LocationReply(String message) {
            this.message = message;
        }
    }

    static public class SkipReply implements Serializable {

        private String status = "OK";
        private boolean completed;
        private String message;
        @SerializedName("scoreAdjustment")
        private int scoreAdjustment;

        public SkipReply(boolean completed, String message, int scoreAdjustment) {
            this.completed = completed;
            this.message = message;
            this.scoreAdjustment = scoreAdjustment;
        }
    }

    static public class ScoreReply implements Serializable {

        private String status = "OK";
        private boolean completed;
        private boolean finished;
        private String player;
        private long score;

        public ScoreReply(boolean completed, boolean finished, String player, long score) {
            this.completed = completed;
            this.finished = finished;
            this.player = player;
            this.score = score;
        }
    }

    static public class LeaderboardReply implements Serializable {

        private String status = "OK";
        @SerializedName("numOfPlayers")
        private int numOfPlayers;
        private boolean sorted;
        private int limit;
        private Vector<LeaderboardEntry> leaderboard;

        public LeaderboardReply(final boolean sorted, final int limit, final Vector<Session> sessions) {
            this.numOfPlayers = sessions.size();
            this.sorted = sorted;
            this.limit = limit;
            this.leaderboard = new Vector<>();
            // add all entries
            for(final Session session : sessions) {
                this.leaderboard.add(new LeaderboardEntry(session.getPlayerName(), session.getScore(), session.getCompletionTime()));
            }

            if(sorted) { // sort if needed
                Collections.sort(leaderboard);
                while(leaderboard.size() > limit) { // remove last item until leaderboard has <= limit
                    leaderboard.remove(leaderboard.size() - 1);
                }
            } else { // shuffle
                Collections.shuffle(leaderboard);
            }
        }
    }

    static public class LeaderboardEntry implements Comparable<LeaderboardEntry>, Serializable {
        private String player;
        private long score;
        @SerializedName("completionTime")
        private long completionTime;

        public LeaderboardEntry(String player, long score, long completionTime) {
            this.player = player;
            this.score = score;
            this.completionTime = completionTime;
        }

        @Override
        public int compareTo(LeaderboardEntry other) {
            // first compare by score (higher is 'before')
            final int scoreCompare = Long.compare(this.score, other.score);
            if(scoreCompare != 0) {
                return -scoreCompare;
            } else {
                // if scores are equal, compare completion times (smaller is 'before' except 0 which means unfinished which is the largest)
                return Long.compare(this.completionTime == 0 ? Integer.MAX_VALUE : this.completionTime,
                        other.completionTime == 0 ? Integer.MAX_VALUE : other.completionTime);
            }
        }

        @Override
        public String toString() {
            return "\nLeaderboardEntry{" +
                    "player='" + player + '\'' +
                    ", score=" + score +
                    ", completionTime=" + completionTime +
                    '}';
        }
    }

    static public class ErrorReply {

        private final String status = "ERROR";

        @SerializedName("errorMessages")
        private final ArrayList<String> errorMessages = new ArrayList<>();

        public ErrorReply(final String errorMessage) {
            this.errorMessages.add(errorMessage);
        }

        public ErrorReply(final ArrayList<String> errorMessages) {
            this.errorMessages.addAll(errorMessages);
        }

        public String getStatus() {
            return status;
        }

        public ArrayList<String> getErrorMessages() {
            return errorMessages;
        }
    }
}