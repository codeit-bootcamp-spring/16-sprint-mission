package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        super();
        Validation.validateNull(userId, "userId");

        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        super.update();
    }

    public boolean isOnline() {
        Instant now = Instant.now();
        Instant onlineSince = now.minus(ONLINE_THRESHOLD);

        return !lastActiveAt.isBefore(onlineSince)
                && !lastActiveAt.isAfter(now);
    }
}
