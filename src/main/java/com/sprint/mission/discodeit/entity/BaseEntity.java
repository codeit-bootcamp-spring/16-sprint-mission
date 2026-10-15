package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {

    private UUID id;
    private Long createdAt;
    protected Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return this.id;
    }

    public Long getCreatedAt() {
        return this.createdAt;
    }

    public Long getUpdatedAt() {
        return this.updatedAt;
    }

    public void update() {
        this.updatedAt = System.currentTimeMillis();
    }

}
