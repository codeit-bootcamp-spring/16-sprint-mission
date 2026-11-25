package com.sprint.mission.discodeit.dto.authentication;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId
) {
}
