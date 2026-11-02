package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity implements Updatable {
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadTime;
    private Instant updatedAt;

    public ReadStatus(UUID userId, UUID channelId) {
        super();
        this.updatedAt = this.getCreatedAt();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadTime = this.getCreatedAt();
    }

    public void update(Instant newLastReadTime) {
        if (newLastReadTime != null && !newLastReadTime.equals(this.lastReadTime)) {
            this.lastReadTime = newLastReadTime;
            this.update();
        }
    }

    @Override
    public void update() {
        this.updatedAt = Instant.now();
    }
}
