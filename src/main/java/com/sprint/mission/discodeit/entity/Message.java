package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private UUID sentFromId;
    private UUID channelId;
    private String content;

    public Message(UUID sentFromId, String content, UUID channelId) {
        super();
        this.sentFromId = sentFromId;
        this.content = content;
        this.channelId = channelId;
    }

    public Message update(Message message) {
        this.sentFromId = message.sentFromId;
        this.content = message.content;
        this.channelId = message.channelId;
        super.update();
        return this;
    }

    public void validateNotDeleted() {
        if (this.isDeleted()) throw new IllegalStateException("삭제된 메세지입니다.");
    }

    public UUID getSentFromId() {
        return this.sentFromId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getChannelId() {
        return this.channelId;
    }

    @Override
    public String toString() {
        return "id: %s, 보낸 사람 (id): %s, 내용: %s, 채널 (id): %s, 상태: %s".formatted(
                this.getId().toString().substring(0, 4),
                this.sentFromId.toString().substring(0, 4), this.content,
                this.channelId.toString().substring(0, 4), super.getStatus());
    }
}
