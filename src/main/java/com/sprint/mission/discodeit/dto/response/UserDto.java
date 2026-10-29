package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID userId, Instant createdAt, Instant updatedAt,
        String username, String email, UUID profileId,
        boolean isOnline
) {}
