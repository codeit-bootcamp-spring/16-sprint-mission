package com.sprint.mission.discodeit.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserDto")
public record UserDto(UUID id,
                      String username,
                      String email,
                      BinaryContentDto profile,
                      boolean online
) {
}
