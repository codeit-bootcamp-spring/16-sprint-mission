package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {

    public static ReadStatusResponse from(ReadStatus status) {
        return new ReadStatusResponse(status.getId(), status.getUserId(),
                status.getChannelId(), status.getLastReadAt());
    }
}
