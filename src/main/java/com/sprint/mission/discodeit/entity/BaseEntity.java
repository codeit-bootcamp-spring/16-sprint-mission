package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    protected BaseEntity() {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        updatedAt = createdAt;
    }

    protected void touch() {
        updatedAt = System.currentTimeMillis();
    };
}
