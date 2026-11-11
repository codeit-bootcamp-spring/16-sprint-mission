package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class Base implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public void updateUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
