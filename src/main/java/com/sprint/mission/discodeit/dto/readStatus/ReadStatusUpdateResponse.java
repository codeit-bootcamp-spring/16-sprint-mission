package com.sprint.mission.discodeit.dto.readStatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateResponse(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadTime
) {
    public static ReadStatusUpdateResponse from(ReadStatus readStatus) {
        return new ReadStatusUpdateResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadTime()
        );
    }
}
