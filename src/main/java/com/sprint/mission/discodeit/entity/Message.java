package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID senderId;
    private UUID receiverId;
    private UUID channelId;
    private String content;

    public Message(UUID senderId, UUID receiverId, UUID channelId, String content) {
        super();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.channelId = channelId;
        this.content = content;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
