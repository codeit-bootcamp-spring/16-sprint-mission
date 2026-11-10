package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private static final Duration ONLINE_THRESHOLD =
            Duration.ofMinutes(5);

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        super();

        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public void updateLastActiveAt(Instant lastActiveAt) {
        if (lastActiveAt == null) {
            throw new IllegalArgumentException("마지막 접속 시간은 필수입니다.");
        }

        this.lastActiveAt = lastActiveAt;
        updateUpdatedAt();
    }

    public boolean isOnline() {
        Instant now = Instant.now();

        return !lastActiveAt.isBefore(now.minus(ONLINE_THRESHOLD))
                && !lastActiveAt.isAfter(now);
    }
}