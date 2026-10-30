package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {

    private final UUID userId;
    private Instant lastSeenAt;

    public UserStatus(UUID userId, Instant lastSeenAt) {
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;
    }

    public void update(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
        updateTimestamp();
    }

    public boolean isOnline() {
        Instant now = Instant.now();

        return lastSeenAt != null
                && !lastSeenAt.isBefore(
                now.minus(Duration.ofMinutes(5))
        )
                && !lastSeenAt.isAfter(now);
    }
}