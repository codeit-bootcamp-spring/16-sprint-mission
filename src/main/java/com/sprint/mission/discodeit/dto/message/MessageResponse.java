package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageResponse(
        String content,
        UUID messageId,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds
) {
}
