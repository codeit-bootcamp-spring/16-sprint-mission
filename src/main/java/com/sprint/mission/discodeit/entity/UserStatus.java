package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserStatus extends Base {
    private UUID userId;
    private Instant lastActiveAt;

    public boolean isOnline() {
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        updateUpdatedAt();
    }
}