package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        boolean online,
        UUID profileId,
        Instant createdAt,
        Instant updatedAt
) {
}
