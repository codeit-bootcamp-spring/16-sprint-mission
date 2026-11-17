package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserResponseDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,

        @Schema(example = "user1234")
        String username,

        @Schema(example = "user1234@example.com")
        String email,

        @Schema(example = "1234")
        String password,

        UUID profileId
) {
}
