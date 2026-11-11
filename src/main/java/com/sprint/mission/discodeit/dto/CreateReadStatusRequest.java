package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusRequest(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}