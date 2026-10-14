package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private String content;
    private final UUID senderId;
    private final UUID channelId;

    public Message(String content, UUID senderId, UUID channelId) {
        super();

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지를 작성해 주세요.");
        }

        Validation.validateBlank(senderId, "senderId");
        Validation.validateBlank(channelId, "channelId");

        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void updateMessage(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지를 작성해 주세요.");
        }

        this.content = content;
        super.update();
    }

    @Override
    public String toString() {
        return "메시지 정보: " +
                "{메시지 내용='" + content + '\'' +
                ", 발신자=" + senderId +
                ", 채널=" + channelId +
                '}';
    }
}