package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {
    private Instant updatedAt;

    protected BaseUpdatableEntity() {
        super();
        updatedAt = getCreatedAt();
    }

    protected void touch() {
        updatedAt = Instant.now();
    }
}