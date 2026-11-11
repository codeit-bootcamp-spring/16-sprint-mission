package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Updatable {
    private final UUID authorId;
    private final UUID channelId;
    private final List<UUID> attachmentIds;
    private Instant updatedAt;
    private String content;

    public Message(UUID authorId, String content, UUID channelId) {
        super();
        this.updatedAt = this.getCreatedAt();
        this.authorId = authorId;
        this.content = content;
        this.channelId = channelId;
        this.attachmentIds = new ArrayList<>();
    }

    public Message update(String newContent) {
        boolean updated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            updated = true;
        }

        if (updated) {
            this.update();
        }

        return this;
    }

    public void addAttachment(UUID attachmentId) {
        this.attachmentIds.add(attachmentId);
        this.update();
    }

    public void removeAttachment(UUID attachmentId) {
        this.attachmentIds.remove(attachmentId);
        this.update();
    }

    @Override
    public void update() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "id: %s, 보낸 사람 (id): %s, 내용: %s, 채널 (id): %s".formatted(
                this.getId().toString().substring(0, 4),
                this.authorId.toString().substring(0, 4),
                this.content,
                this.channelId.toString().substring(0, 4));
    }
}
