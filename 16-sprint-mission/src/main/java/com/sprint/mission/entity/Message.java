package com.sprint.mission.entity;

public class Message extends Entity {

    private String message;

    public Message(String createdAt, String updatedAt, String message) {
        super(createdAt, updatedAt);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
