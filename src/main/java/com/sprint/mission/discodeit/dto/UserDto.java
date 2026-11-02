package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserDto {
    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String username;
    private final String email;
    private final boolean online;

    public UserDto(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            boolean online
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.username = username;
        this.email = email;
        this.online = online;
    }
}