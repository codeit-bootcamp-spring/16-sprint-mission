package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public void update(Instant lastActiveAt) {
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            this.updatedAt = Instant.now();
        }
    }

    // 마지막 접속 시간이 현재로부터 5분 이내인지 확인
    public boolean isOnline() {
        return !lastActiveAt.isBefore(Instant.now().minus(Duration.ofMinutes(5)));
    }
}