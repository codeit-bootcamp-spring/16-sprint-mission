package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
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

    public String getContent() { return content; }
    public UUID getSenderId() { return senderId; }
    public UUID getChannelId() { return channelId; }

    public void updateMessage(String content) {
        Validation.validateBlank(content, "message");
        this.content = content;
        super.update();
    }
}
