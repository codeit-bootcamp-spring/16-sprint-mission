package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContentCreateRequest> attachments
) {
    public MessageCreateRequest {
        attachments = attachments == null ? List.of() : List.copyOf(attachments);
    }
}
