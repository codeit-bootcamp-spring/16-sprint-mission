package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public abstract class Base implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
