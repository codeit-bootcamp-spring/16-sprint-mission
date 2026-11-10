package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class MessageCreateRequest {

    private final UUID channelId;
    private final UUID authorId;
    private final String content;
    private final List<BinaryContentCreateRequest> attachments;

    public MessageCreateRequest(
            UUID channelId,
            UUID authorId,
            String content,
            List<BinaryContentCreateRequest> attachments
    ) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
        this.attachments = attachments == null
                ? List.of()
                : List.copyOf(attachments);
    }
}