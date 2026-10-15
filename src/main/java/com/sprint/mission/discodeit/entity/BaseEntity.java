package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    protected BaseEntity() {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    protected void touch() {
        updatedAt = System.currentTimeMillis();
    };
}
