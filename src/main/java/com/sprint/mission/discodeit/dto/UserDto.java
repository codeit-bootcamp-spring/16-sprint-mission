package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserDto")
public record UserDto(UUID id,
                      String username,
                      String email,
                      UUID profileId,
                      Instant createdAt,
                      Instant updatedAt,
                      boolean online
) {
}
