package com.sprint.mission.entity;

import java.util.UUID;

public abstract class Entity {

    protected String id;
    protected String createdAt;
    protected String updatedAt;

    public Entity(String createdAt, String updatedAt) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
