package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID profileId,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        boolean isOnline
) {
}
