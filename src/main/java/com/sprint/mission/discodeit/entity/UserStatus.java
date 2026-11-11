package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastActiveAt = Instant.now();
        this.createdAt = Instant.now();
    }

    public void update(Instant lastActiveAt) {
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            this.updatedAt = Instant.now();
        }
    }

    // 마지막 활동 후 지난 시간 계산
    private Duration getInactiveDuration() {
        return Duration.between(lastActiveAt, Instant.now());
    }

    // 활동 중이면 true, 아니면 false 리턴
    public boolean isOnline() {
        return getInactiveDuration().compareTo(ONLINE_THRESHOLD) < 0;
    }
}
