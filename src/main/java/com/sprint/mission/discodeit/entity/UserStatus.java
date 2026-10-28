package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseUpdatableEntity {
    private final UUID userId;
    private Instant lastActiveAt;

    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

    @Serial
    private static final long serialVersionUID = 1L;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        super();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void updateLastContactAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        touch();
    }

    public boolean isOnline() {
        return lastActiveAt.isAfter(Instant.now().minus(ONLINE_THRESHOLD));
    }
}
