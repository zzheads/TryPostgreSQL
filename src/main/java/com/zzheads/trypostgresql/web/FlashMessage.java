package com.zzheads.trypostgresql.web;

import com.google.gson.Gson;

public class FlashMessage {
    private String message;
    private Status status;
    private Gson gson = new Gson();

    public FlashMessage(String message, Status status) {
        this.message = message;
        this.status = status;
    }

    public enum Status {
        SUCCESS,
        INFO,
        FAILURE
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String toJson () {
        return gson.toJson(this);
    }
}
