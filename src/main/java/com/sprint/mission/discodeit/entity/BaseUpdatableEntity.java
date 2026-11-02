package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.io.Serial;
import java.time.Instant;

@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {
    private Instant updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;

    protected BaseUpdatableEntity() {
        super();
        updatedAt = getCreatedAt();
    }

    protected void touch() {
        updatedAt = Instant.now();
    }
}