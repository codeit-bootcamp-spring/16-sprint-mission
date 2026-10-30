package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest(
        String content,
        UUID userId,
        UUID channelId,
        List<CreateBinaryContentRequest> attachments
) {
}