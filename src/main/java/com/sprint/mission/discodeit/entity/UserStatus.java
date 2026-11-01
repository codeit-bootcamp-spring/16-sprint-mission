package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Duration ONLINE_TIMEOUT = Duration.ofMinutes(5);

    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(UUID userId) {
        this.userId = userId;
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        if (updatedAt == null) {
            return false;
        }
        return Duration.between(updatedAt, Instant.now())
                .compareTo(ONLINE_TIMEOUT) <= 0;
    }

    public void updateLastAccess() {
        this.updatedAt = Instant.now();
    }
}
