package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        Boolean online
) {
}

