package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity implements Updatable {
    private final UUID userId;
    private Instant lastActiveTime;
    private Instant updatedAt;

    public UserStatus(UUID userId, Instant lastActiveTime) {
        super();
        this.updatedAt = this.getCreatedAt();
        this.userId = userId;
        this.lastActiveTime = lastActiveTime;
    }

    public void update(Instant newLastActiveTime) {
        if (newLastActiveTime != null && !newLastActiveTime.equals(this.lastActiveTime)) {
            this.lastActiveTime = newLastActiveTime;
            this.update();
        }
    }

    public boolean isUserOnline() {
        if (this.lastActiveTime == null) return false;
        Duration duration = Duration.between(this.lastActiveTime, Instant.now());

        // 최근 5분 이내 활동했으면 활동 중.
        return duration.getSeconds() < 60 * 5;
    }

    @Override
    public void update() {
        this.updatedAt = Instant.now();
    }
}
