package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    private static final long serialVersionUID = 2L;

    private String content;
    private final UUID senderId;
    private final UUID channelId;

    public Message(String content, UUID senderId, UUID channelId) {
        super();
        Validation.validateBlank(content, "message");
        Validation.validateNull(senderId, "senderId");
        Validation.validateNull(channelId, "channelId");
        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    public void updateMessage(String content) {
        Validation.validateBlank(content, "message");
        this.content = content;
        super.update();
    }
}
