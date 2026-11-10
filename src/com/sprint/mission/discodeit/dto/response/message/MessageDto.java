package com.sprint.mission.discodeit.dto.response.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID authorId,
        UUID channelId,
        List<UUID> attachmentIds
) {
    public MessageDto {
        attachmentIds = List.copyOf(attachmentIds);
    }
}