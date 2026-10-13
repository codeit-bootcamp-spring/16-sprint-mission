package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity{
    private UUID senderId;
    private UUID receiverId;
    private String content;

    public Message(UUID senderId, UUID receiverId, String content) {
        super();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
        super.update();
    }
}
