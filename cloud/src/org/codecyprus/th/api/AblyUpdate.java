package org.codecyprus.th.api;

import java.io.Serializable;

public class AblyUpdate implements Serializable {

    private String uuid;
    private String app;
    private String player;
    private long score;
    private long completionTime;
    private double latitude;
    private double longitude;

    public AblyUpdate(String uuid, String app, String player, long score, long completionTime, double latitude, double longitude) {
        this.uuid = uuid;
        this.app = app;
        this.player = player;
        this.score = score;
        this.completionTime = completionTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUuid() {
        return uuid;
    }

    public String getApp() {
        return app;
    }

    public String getPlayer() {
        return player;
    }

    public long getScore() {
        return score;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}