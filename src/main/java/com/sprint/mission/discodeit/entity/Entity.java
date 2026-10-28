package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public abstract class Entity implements Serializable {

    protected UUID id;
    protected Long createdAt;
    protected Long updatedAt;

    public Entity() {
        long epochMilli = Instant.now().toEpochMilli();
        this.id = UUID.randomUUID();
        this.createdAt = epochMilli;
        this.updatedAt = epochMilli;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    protected void touch() {
        this.updatedAt = Instant.now().toEpochMilli();
    }
}
