package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Message extends BaseEntity {
    private final UUID senderId;
    private final UUID channelId; // 채널ID를 속성으로 삼아야 채널 검증 가능
    private String content;

    private static final long serialVersionUID = 1L;

    public Message(UUID senderId, UUID channelId, String content) {
        super();
        this.senderId = Objects.requireNonNull(senderId, "발신자는 필수입니다.");
        this.channelId = Objects.requireNonNull(channelId, "채널ID는 필수입니다.");
        this.content = content;
    }

    public UUID getSenderId() { return senderId; }
    public UUID getChannelId() { return channelId; }
    public String getContent() { return content; }

    public void update(String content) {
        this.content = content;
        touch();
    }
}
