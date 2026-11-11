package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    public static final long serialVersionUID = 1L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastReadAt;
    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = lastReadAt;
        this.updatedAt = lastReadAt;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
    public void updateLastReadAt(Instant newReadAt) {
        if (newReadAt != null && !newReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newReadAt;
            this.updatedAt = newReadAt;
        }
    }
}
