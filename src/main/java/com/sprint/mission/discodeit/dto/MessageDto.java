package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto (UUID id,
                          String content,
                          UUID userId,
                          UUID channelId,
                          List<UUID> attachmentIds,
                          Instant createdAt,
                          Instant updatedAt) {
}
