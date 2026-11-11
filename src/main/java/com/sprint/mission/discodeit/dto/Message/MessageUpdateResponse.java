package com.sprint.mission.discodeit.dto.Message;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageUpdateResponse(
        UUID id,
        UUID authorId,
        UUID channelId,
        String content,
        Instant updatedAt,
        List<UUID> attachmentIds
) {
    public static MessageUpdateResponse from(Message message) {
        return new MessageUpdateResponse(
                message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getContent(),
                message.getUpdatedAt(),
                List.copyOf(message.getAttachmentIds())
        );
    }
}
