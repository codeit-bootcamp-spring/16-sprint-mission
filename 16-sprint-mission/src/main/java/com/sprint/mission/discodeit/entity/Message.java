package com.sprint.mission.discodeit.entity;

public class Message extends Entity {

    private String message;
    private final String channelId;
    private final String userId;

    public Message(String message, String channelId, String userId) {
        this.message = message;
        this.channelId = channelId;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
