package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {

    private String content;
    private UUID userId;
    private UUID channelId;

    public Message(String content, UUID userId, UUID channelId) {
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void update(String content) {
        this.content = content;
        updateTimestamp();
    }
}