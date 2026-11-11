package com.sprint.mission.discodeit.dto.ReadStatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusFindResponse(
        UUID userId,
        UUID channelId,
        Instant lastReadTime
) {
    public static ReadStatusFindResponse from(ReadStatus readStatus) {
        return new ReadStatusFindResponse(
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadTime()
        );
    }
}
