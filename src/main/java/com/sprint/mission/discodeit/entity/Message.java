package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private String content;
    private final UUID channelId;
    private final UUID authorId;
    private final List<UUID> attachmentIds;
    private final MessageType type;

    public Message(
            String content,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds,
            MessageType type
    ) {
        super();

        Validation.validateBlank(content, "content");
        Validation.validateNull(channelId, "channelId");
        Validation.validateNull(authorId, "authorId");
        Validation.validateNull(attachmentIds, "attachmentIds");
        Validation.validateNull(type, "type");

        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = List.copyOf(attachmentIds);
        this.type = type;
    }

    public void updateContent(String content) {
        Validation.validateBlank(content, "content");
        this.content = content;
        super.update();
    }
}
