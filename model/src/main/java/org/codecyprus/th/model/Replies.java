package org.codecyprus.th.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

public class Replies {

    static public class ListReply extends Reply {

        @SerializedName("treasureHunts")
        private final Vector<TreasureHunt> selectedTreasureHunts;

        public ListReply(Vector<TreasureHunt> selectedTreasureHunts) {
            super(Status.OK);
            this.selectedTreasureHunts = selectedTreasureHunts;
        }

        public Vector<TreasureHunt> getSelectedTreasureHunts() {
            return selectedTreasureHunts;
        }
    }

    static public class StartReply extends Reply {

        @SerializedName("session")
        private String sessionId;
        @SerializedName("numOfQuestions")
        private int numOfQuestions;

        public StartReply(String sessionId, int numOfQuestions) {
            super(Status.OK);
            this.sessionId = sessionId;
            this.numOfQuestions = numOfQuestions;
        }

        public String getSessionId() {
            return sessionId;
        }

        public int getNumOfQuestions() {
            return numOfQuestions;
        }
    }

    static public class QuestionReply extends Reply {

        private boolean completed;

        private String questionText;

        private QuestionType questionType;

        private boolean canBeSkipped;

        private boolean requiresLocation;

        private int numOfQuestions;

        private int currentQuestionIndex;

        private long correctScore;

        private long wrongScore;

        private long skipScore;

        public QuestionReply(boolean completed, String questionText, QuestionType questionType, boolean canBeSkipped,
                             boolean requiresLocation, int numOfQuestions, int currentQuestionIndex, long correctScore,
                             long wrongScore, long skipScore) {
            super(Status.OK);
            this.completed = completed;
            this.questionText = questionText;
            this.questionType = questionType;
            this.canBeSkipped = canBeSkipped;
            this.requiresLocation = requiresLocation;
            this.numOfQuestions = numOfQuestions;
            this.currentQuestionIndex = currentQuestionIndex;
            this.correctScore = correctScore;
            this.wrongScore = wrongScore;
            this.skipScore = skipScore;
        }

        public boolean isCompleted() {
            return completed;
        }

        public String getQuestionText() {
            return questionText;
        }

        public QuestionType getQuestionType() {
            return questionType;
        }

        public boolean isCanBeSkipped() {
            return canBeSkipped;
        }

        public boolean isRequiresLocation() {
            return requiresLocation;
        }

        public int getNumOfQuestions() {
            return numOfQuestions;
        }

        public int getCurrentQuestionIndex() {
            return currentQuestionIndex;
        }

        public long getCorrectScore() {
            return correctScore;
        }

        public long getWrongScore() {
            return wrongScore;
        }

        public long getSkipScore() {
            return skipScore;
        }
    }

    static public class AnswerReply extends Reply {

        private boolean correct;
        private boolean completed;
        private String message;
        private int scoreAdjustment;

        public AnswerReply(boolean correct, boolean completed, String message, int scoreAdjustment) {
            super(Status.OK);
            this.correct = correct;
            this.completed = completed;
            this.message = message;
            this.scoreAdjustment = scoreAdjustment;
        }

        public boolean isCorrect() {
            return correct;
        }

        public boolean isCompleted() {
            return completed;
        }

        public String getMessage() {
            return message;
        }

        public int getScoreAdjustment() {
            return scoreAdjustment;
        }
    }

    static public class LocationReply extends Reply {

        private String message;

        public LocationReply(String message) {
            super(Status.OK);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    static public class SkipReply extends Reply {

        private boolean completed;
        private String message;
        @SerializedName("scoreAdjustment")
        private int scoreAdjustment;

        public SkipReply(boolean completed, String message, int scoreAdjustment) {
            super(Status.OK);
            this.completed = completed;
            this.message = message;
            this.scoreAdjustment = scoreAdjustment;
        }

        public boolean isCompleted() {
            return completed;
        }

        public String getMessage() {
            return message;
        }

        public int getScoreAdjustment() {
            return scoreAdjustment;
        }
    }

    static public class ScoreReply extends Reply {

        private boolean completed;
        private boolean finished;
        private String player;
        private long score;
        private boolean hasPrize;

        public ScoreReply(boolean completed, boolean finished, String player, long score, boolean hasPrize) {
            super(Status.OK);
            this.completed = completed;
            this.finished = finished;
            this.player = player;
            this.score = score;
            this.hasPrize = hasPrize;
        }

        public boolean isCompleted() {
            return completed;
        }

        public boolean isFinished() {
            return finished;
        }

        public String getPlayer() {
            return player;
        }

        public long getScore() {
            return score;
        }

        public boolean isHasPrize() {
            return hasPrize;
        }
    }

    static public class LeaderboardReply extends Reply {

        @SerializedName("numOfPlayers")
        private int numOfPlayers;
        private boolean sorted;
        private int limit;
        private boolean hasPrize;
        private Vector<LeaderboardEntry> leaderboard;
        private String treasureHuntName;

        public LeaderboardReply(final boolean sorted, final int limit, final boolean hasPrize, final Vector<Session> sessions, final String treasureHuntName) {
            super(Status.OK);
            this.numOfPlayers = sessions.size();
            this.sorted = sorted;
            this.limit = limit;
            this.hasPrize = hasPrize;
            this.leaderboard = new Vector<>();
            this.treasureHuntName = treasureHuntName;
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

        public int getNumOfPlayers() {
            return numOfPlayers;
        }

        public boolean isSorted() {
            return sorted;
        }

        public int getLimit() {
            return limit;
        }

        public boolean isHasPrize() {
            return hasPrize;
        }

        public Vector<LeaderboardEntry> getLeaderboard() {
            return leaderboard;
        }

        public String getTreasureHuntName() {
            return treasureHuntName;
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

    static public class LeaderboardWithLocationReply extends Reply {

        @SerializedName("numOfPlayers")
        private int numOfPlayers;
        private boolean sorted;
        private int limit;
        private boolean hasPrize;
        private String name;
        private String treasureHuntName;
        private long startsOn;
        private long endsOn;
        private Vector<LeaderboardWithLocationEntry> leaderboard;

        public LeaderboardWithLocationReply(final boolean sorted, final int limit, final boolean hasPrize, final long startsOn, final long endsOn, final Vector<Session> sessions, final String treasureHuntName, final Map<String,Coordinates> latestCoordinates) {
            super(Status.OK);
            this.numOfPlayers = sessions.size();
            this.sorted = sorted;
            this.limit = limit;
            this.hasPrize = hasPrize;
            this.name = treasureHuntName;
            this.treasureHuntName = treasureHuntName;
            this.startsOn = startsOn;
            this.endsOn = endsOn;
            this.leaderboard = new Vector<>();
            // add all entries
            for(final Session session : sessions) {
                final Coordinates coordinates = latestCoordinates.get(session.getUuid());
                final double latitude = coordinates == null ? 0d : coordinates.getLatitude();
                final double longitude = coordinates == null ? 0d : coordinates.getLongitude();
                this.leaderboard.add(new LeaderboardWithLocationEntry(session.getPlayerName(), session.getScore(), session.getCompletionTime(), latitude, longitude));
            }

            // always sort
            Collections.sort(leaderboard);
        }

        public int getNumOfPlayers() {
            return numOfPlayers;
        }

        public boolean isSorted() {
            return sorted;
        }

        public int getLimit() {
            return limit;
        }

        public boolean isHasPrize() {
            return hasPrize;
        }

        public String getName() {
            return name;
        }

        public String getTreasureHuntName() {
            return treasureHuntName;
        }

        public Vector<LeaderboardWithLocationEntry> getLeaderboard() {
            return leaderboard;
        }
    }

    static public class LeaderboardWithLocationEntry implements Comparable<LeaderboardWithLocationEntry>, Serializable {
        private String player;
        private long score;
        @SerializedName("completionTime")
        private long completionTime;
        private double latitude;
        private double longitude;

        public LeaderboardWithLocationEntry(String player, long score, long completionTime, double latitude, double longitude) {
            this.player = player;
            this.score = score;
            this.completionTime = completionTime;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public int compareTo(LeaderboardWithLocationEntry other) {
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
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    static public class ErrorReply extends Reply {

        @SerializedName("errorMessages")
        private final ArrayList<String> errorMessages = new ArrayList<>();

        public ErrorReply(final String errorMessage) {
            super(Status.ERROR);
            this.errorMessages.add(errorMessage);
        }

        public ErrorReply(final ArrayList<String> errorMessages) {
            super(Status.ERROR);
            this.errorMessages.addAll(errorMessages);
        }

        public ArrayList<String> getErrorMessages() {
            return errorMessages;
        }
    }

    static public class Reply {

        protected final Status status;

        public Reply(final Status status) {
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }
    }
}