package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusDto {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final UUID userId;
    private final Instant lastActiveAt;
    private final boolean online;

    public UserStatusDto(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            Instant lastActiveAt,
            boolean online
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
        this.online = online;
    }
}

