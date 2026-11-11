package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private Instant lastSeenAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.userId = userId;
        this.lastSeenAt = now;
    }

    public void updateLastSeen() {
        Instant now = Instant.now();
        this.lastSeenAt = now;
        this.updatedAt = now;
    }

    public boolean isOnline() {
        Instant now = Instant.now();
        // 현재 시간에서 5분을 뺀 기준
        Instant threshold = now.minus(5, ChronoUnit.MINUTES);
        return this.lastSeenAt.isAfter(threshold) || this.lastSeenAt.equals(threshold);
    }
}
