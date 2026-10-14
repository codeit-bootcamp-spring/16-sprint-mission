package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends Entity {

    private String message;
    private final UUID channelId;
    private final UUID userId;

    public Message(String message, UUID channelId, UUID userId) {
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

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getUserId() {
        return userId;
    }
}
