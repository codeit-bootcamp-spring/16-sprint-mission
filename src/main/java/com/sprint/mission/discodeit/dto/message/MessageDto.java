package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        Instant createdAt,
        String content,
        UUID authorId,
        UUID channelId,
        List<UUID> attachmentIds
) {
}
