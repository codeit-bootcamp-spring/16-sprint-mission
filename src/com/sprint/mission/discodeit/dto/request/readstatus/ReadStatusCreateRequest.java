package com.sprint.mission.discodeit.dto.request.readstatus;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId
) {
}