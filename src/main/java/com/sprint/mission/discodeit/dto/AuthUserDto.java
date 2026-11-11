package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthUserDto(UUID id,
                          String username,
                          String email,
                          UUID profileId,
                          Instant createdAt,
                          Instant updatedAt) {
}
