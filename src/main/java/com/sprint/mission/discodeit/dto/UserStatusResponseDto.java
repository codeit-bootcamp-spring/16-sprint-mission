package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(
        UUID userStatusId,
        UUID userId,
        Instant updatedAt
) {}
