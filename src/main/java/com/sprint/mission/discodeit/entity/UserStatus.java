
package com.sprint.mission.discodeit.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus extends BaseEntity {

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        updateTimestamp();
    }

    public boolean isOnline() {
        if (lastActiveAt == null) {
            return false;
        }

        Instant now = Instant.now();

        return !lastActiveAt.isAfter(now)
                && !lastActiveAt.isBefore(now.minus(Duration.ofMinutes(5)));
    }
}
