package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "UserStatusDto")
public record UserStatusDto(
        UUID id,
        UUID userId,
        Instant lastActiveAt
) {
}
