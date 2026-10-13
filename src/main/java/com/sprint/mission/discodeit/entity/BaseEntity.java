package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class BaseEntity {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();
    }

    public UUID getId() {
        return this.id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    protected BaseEntity update() {
        this.updatedAt = Instant.now().toEpochMilli();
        return this;
    }
}
