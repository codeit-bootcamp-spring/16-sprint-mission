package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public class Message extends BaseEntity{
    private UUID senderId;
    private UUID receiverId;
    private UUID channelId; // 채널ID를 속성으로 삼아야 채널 검증 가능
    private String content;

    public Message(UUID senderId, UUID receiverId, UUID channelId, String content) {
        super();
        this.senderId = Objects.requireNonNull(senderId, "발신자는 필수입니다.");
        this.receiverId = Objects.requireNonNull(receiverId, "수신자는 필수입니다.");
        this.channelId = Objects.requireNonNull(channelId, "채널ID는 필수입니다.");

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

    public UUID getChannelId() {
        return channelId;
    }

    public void updateContent(String content) {
        this.content = content;
        super.update();
    }
}
