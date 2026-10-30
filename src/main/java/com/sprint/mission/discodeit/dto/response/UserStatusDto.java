package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
        UUID id, Instant createdAt, Instant updatedAt,
        UUID userId, Instant lastReadAt
) {}
