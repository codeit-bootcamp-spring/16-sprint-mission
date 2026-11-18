package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,

        @Schema(example = "user1234")
        String username,

        @Schema(example = "user1234@example.com")
        String email,

        UUID profileId,
        Boolean online
) {
}

