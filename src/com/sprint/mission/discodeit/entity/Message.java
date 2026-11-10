package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String content;
    private final UUID authorId;
    private final UUID channelId;
    private List<UUID> attachmentIds = new ArrayList<>();

    public Message(String content, UUID authorId, UUID channelId) {
        super();

        validateContent(content);
        validateId(authorId, "작성자 ID");
        validateId(channelId, "채널 ID");

        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    // 외부에서 첨부파일 목록을 직접 수정하지 못하도록 보호
    public List<UUID> getAttachmentIds() {
        return Collections.unmodifiableList(attachmentIds);
    }

    public void update(String content) {
        validateContent(content);

        this.content = content;
        updateUpdatedAt();
    }

    public void addAttachment(UUID attachmentId) {
        if (attachmentId == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        if (!attachmentIds.contains(attachmentId)) {
            attachmentIds.add(attachmentId);
            updateUpdatedAt();
        }
    }

    public void removeAttachment(UUID attachmentId) {
        if (attachmentIds.remove(attachmentId)) {
            updateUpdatedAt();
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }
    }

    private void validateId(UUID id, String fieldName) {
        if (id == null) {
            throw new IllegalArgumentException(fieldName + "는 필수입니다.");
        }
    }
}