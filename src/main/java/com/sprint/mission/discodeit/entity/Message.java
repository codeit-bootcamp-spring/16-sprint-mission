package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private final MessageType type;
    private String content;
    private final UUID senderId;
    private final UUID channelId;
    private final List<UUID> attachmentIds;

    public Message(
            String content,
            UUID senderId,
            UUID channelId
    ) {
        this(content, senderId, channelId, MessageType.USER, List.of());
    }

    public Message(
            String content,
            UUID senderId,
            UUID channelId,
            MessageType type
    ) {
        this(content, senderId, channelId, type, List.of());
    }

    public Message(
            String content,
            UUID senderId,
            UUID channelId,
            MessageType type,
            List<UUID> attachmentIds
    ) {
        super();

        Validation.validateBlank(content, "content");
        Validation.validateNull(senderId, "senderId");
        Validation.validateNull(channelId, "channelId");
        Validation.validateNull(type, "type");
        Validation.validateNull(attachmentIds, "attachmentIds");

        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
        this.type = type;
        this.attachmentIds = List.copyOf(attachmentIds);
    }

    public void updateContent(String content) {
        Validation.validateBlank(content, "content");
        this.content = content;
        super.update();
    }
}
