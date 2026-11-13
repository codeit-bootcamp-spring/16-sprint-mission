package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        super();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public boolean isOnline() {
        return !lastActiveAt.isBefore(Instant.now().minus(Duration.ofMinutes(5)));
    }

    public void updateLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        updateUpdatedAt();
    }
}