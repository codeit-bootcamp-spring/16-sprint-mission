package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import java.io.Serializable;

public class BaseEntity implements Serializable {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private static final long serialVersionUID = 1L;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
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

    protected void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}