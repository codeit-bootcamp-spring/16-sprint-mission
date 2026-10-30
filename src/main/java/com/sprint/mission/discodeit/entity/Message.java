package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    private String content;
    private final UUID userId;
    private final UUID channelId;

    private final Set<UUID> attachmentIds = new LinkedHashSet<>();

    public Message(String content, UUID userId, UUID channelId) {
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public Set<UUID> getAttachmentIds() {
        return Collections.unmodifiableSet(attachmentIds);
    }

    public void addAttachment(UUID attachmentId) {
        attachmentIds.add(attachmentId);
        updateTimestamp();
    }

    public void update(String content) {
        this.content = content;
        updateTimestamp();
    }
}