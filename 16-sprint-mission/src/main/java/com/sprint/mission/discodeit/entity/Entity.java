package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public abstract class Entity {

    protected String id;
    protected long createdAt;
    protected long updatedAt;

    public Entity() {
        long epochSecond = Instant.now().getEpochSecond();
        this.id = UUID.randomUUID().toString();
        this.createdAt = epochSecond;
        this.updatedAt = epochSecond;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
