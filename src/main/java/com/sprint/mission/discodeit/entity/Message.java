package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // Message field
    private String content;
    private final UUID userId;
    private final UUID channelId;

    public Message(String content, UUID userId, UUID channelId) {
        super();
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
        updateUpdatedAt();
    }
}
