package org.codecyprus.th.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ErrorReply {

    private final String status = "ERROR";

    @SerializedName("error-messages")
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