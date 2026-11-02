package com.sprint.mission.discodeit.dto.request.message;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID authorId,
        UUID channelId,
        List<BinaryContentCreateRequest> attachments
) {
}