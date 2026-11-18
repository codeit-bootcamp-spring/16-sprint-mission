package com.sprint.mission.discodeit.dto.readStatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusFindResponse(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadTime
) {
    public static ReadStatusFindResponse from(ReadStatus readStatus) {
        return new ReadStatusFindResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadTime()
        );
    }
}
