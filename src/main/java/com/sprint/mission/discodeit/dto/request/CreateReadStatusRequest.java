package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record CreateReadStatusRequest(
        UUID userId,
        UUID channelId
) {
}
