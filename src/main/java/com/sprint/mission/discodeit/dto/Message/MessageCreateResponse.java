package com.sprint.mission.discodeit.dto.Message;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageCreateResponse(
        UUID id,
        UUID authorId,
        UUID channelId,
        String content,
        Instant createdAt,
        List<UUID> attachmentIds
) {
    public static MessageCreateResponse from(Message message) {
        return new MessageCreateResponse(
                message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getContent(),
                message.getCreatedAt(),
                List.copyOf(message.getAttachmentIds())
        );
    }
}
