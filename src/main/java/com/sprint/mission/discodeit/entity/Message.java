package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String content;

    private UUID channelId;
    private UUID authorId;

    // 메시지에 첨부된 파일들의 BinaryContent ID 목록
    private List<UUID> attachmentIds = List.of();

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;

        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    // 메시지 첨부파일 목록 변경
    public void replaceAttachments(List<UUID> attachmentIds) {
        this.attachmentIds = List.copyOf(attachmentIds);
        this.updatedAt = Instant.now();
    }
}