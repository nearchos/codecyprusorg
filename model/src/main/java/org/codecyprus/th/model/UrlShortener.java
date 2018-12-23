package org.codecyprus.th.model;

import java.io.Serializable;

public class UrlShortener implements Serializable {

    private String uuid; // PK
    private String key; // must be unique
    private String target; // an absolute URL

    public UrlShortener(String uuid, String key, String target) {
        this.uuid = uuid;
        this.key = key;
        this.target = target;
    }

    public UrlShortener(String key, String target) {
        this(null, key, target);
    }

    public String getUuid() {
        return uuid;
    }

    public String getKey() {
        return key;
    }

    public String getTarget() {
        return target;
    }
}