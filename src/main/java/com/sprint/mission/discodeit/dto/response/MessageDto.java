package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id, Instant createdAt, Instant updatedAt,
        UUID authorId, UUID channelId,String content,
        List<UUID> attachmentIds
) {}
