package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    private final UUID id;
    private final Instant createdAt;

    protected BaseEntity() {
        id = UUID.randomUUID();
        createdAt = Instant.now();
    }
}
