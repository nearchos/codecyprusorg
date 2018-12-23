package org.codecyprus.th.model;

import java.io.Serializable;

public class Timed implements Serializable {

    private String uuid; // PK
    private String treasureHuntUuid; // FK
    private String titleText;
    private String bodyText; // can be HTML

    public Timed(String treasureHuntUuid, String titleText, String bodyText) {
        this(null, treasureHuntUuid, titleText, bodyText);
    }

    public Timed(String uuid, String treasureHuntUuid, String titleText, String bodyText) {
        this.uuid = uuid;
        this.treasureHuntUuid = treasureHuntUuid;
        this.titleText = titleText;
        this.bodyText = bodyText;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTreasureHuntUuid() {
        return treasureHuntUuid;
    }

    public String getTitleText() {
        return titleText;
    }

    public String getBodyText() {
        return bodyText;
    }
}