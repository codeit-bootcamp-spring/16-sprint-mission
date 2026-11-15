package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record UserResponseDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        String password,
        UUID profileId
) {
}
