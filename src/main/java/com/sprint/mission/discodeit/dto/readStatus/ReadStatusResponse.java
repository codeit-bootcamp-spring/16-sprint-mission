package com.sprint.mission.discodeit.dto.readStatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
    public static ReadStatusResponse from(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadTime()
        );
    }
}
