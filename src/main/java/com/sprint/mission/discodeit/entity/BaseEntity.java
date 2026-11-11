package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    private final UUID id;
    private final Instant createdAt;

    @Serial
    private static final long serialVersionUID = 1L;

    protected BaseEntity() {
        id = UUID.randomUUID();
        createdAt = Instant.now();
    }
}
