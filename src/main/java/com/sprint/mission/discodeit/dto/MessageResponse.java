package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        String content,
        UUID userId,
        UUID channelId,
        Set<UUID> attachmentIds,
        Instant createdAt,
        Instant updatedAt
) {
}