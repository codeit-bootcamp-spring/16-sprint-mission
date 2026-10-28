package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private final MessageType type;
    private String content;
    private final UUID senderId;
    private final UUID channelId;

    public Message(String content, UUID senderId, UUID channelId) {
        this(content, senderId, channelId, MessageType.USER);
    }

    public Message(String content, UUID senderId, UUID channelId, MessageType type) {
        super();
        Validation.validateBlank(content, "message");
        Validation.validateNull(senderId, "senderId");
        Validation.validateNull(channelId, "channelId");
        Validation.validateNull(type, "type");

        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
        this.type = type;
    }

    public void updateContent(String content) {
        Validation.validateBlank(content, "content");
        this.content = content;
        super.update();
    }
}
