package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public abstract class Entity {

    protected UUID id;
    protected long createdAt;
    protected long updatedAt;

    public Entity() {
        long epochSecond = Instant.now().getEpochSecond();
        this.id = UUID.randomUUID();
        this.createdAt = epochSecond;
        this.updatedAt = epochSecond;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
