package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class MessageCreateRequest {
    private final UUID channelId;
    private final UUID authorId;
    private final String content;
    private final List<UUID> attachmentIds;

    public MessageCreateRequest(
            UUID channelId,
            UUID authorId,
            String content,
            List<UUID> attachmentIds
    ) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
        this.attachmentIds = attachmentIds;
    }
}