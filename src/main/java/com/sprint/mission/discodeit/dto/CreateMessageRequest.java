package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record CreateMessageRequest(
        String content,
        UUID userId,
        UUID channelId
) {
}