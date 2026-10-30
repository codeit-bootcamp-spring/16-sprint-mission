package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message extends BaseUpdatableEntity {
    private final UUID authorId;
    private final UUID channelId; // 채널ID를 속성으로 삼아야 채널 검증 가능
    private String content;
    private final List<UUID> attachmentIds;

    @Serial
    private static final long serialVersionUID = 1L;

    public Message(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
        super();
        this.authorId = Objects.requireNonNull(authorId, "발신자는 필수입니다.");
        this.channelId = Objects.requireNonNull(channelId, "채널ID는 필수입니다.");
        this.content = content;
        this.attachmentIds = attachmentIds;
    }

    public void update(String content) {
        this.content = content;
        touch();
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId=" + authorId +
                ", channelId=" + channelId +
                ", content='" + content + '\'' +
                '}';
    }
}
