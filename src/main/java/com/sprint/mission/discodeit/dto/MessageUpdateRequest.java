package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class MessageUpdateRequest {
    private final UUID messageId;
    private final String content;
    private final List<UUID> attachmentIds;

    public MessageUpdateRequest(
            UUID messageId,
            String content,
            List<UUID> attachmentIds
    ) {
        this.messageId = messageId;
        this.content = content;
        this.attachmentIds = attachmentIds;
    }
}