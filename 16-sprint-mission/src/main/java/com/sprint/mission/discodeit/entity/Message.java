package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;


public class Message {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String comment;
    private UUID userId;
    private UUID channelId;

    public UUID getId() {
        return this.id;
    }

    public String getComment() {
        return this.comment;
    }

    public Long getCreatedAt() {
        return this.createdAt;
    }

    public Long getUpdatedAt() {
        return this.updatedAt;
    }

    public UUID getUserId() {
        return this.userId;
    }
    public UUID getChannelId() {
        return this.channelId;
    }

    public Message(String comment, UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.comment = comment;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(String comment) {
        this.comment = comment;
        this.updatedAt = Instant.now().getEpochSecond();
    }
}
