package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Message extends Base{
    private String content;
    private final UUID authorId;
    private final UUID channelId;
    private List<UUID> attachmentIds;

    public void update(String content) {
        this.content = content;
        updateUpdatedAt();
    }

    public void addAttachments(List<UUID> newAttachmentIds) {
        List<UUID> combinedIds =  new ArrayList<>(attachmentIds);
        combinedIds.addAll(newAttachmentIds);
        this.attachmentIds = combinedIds;
    }

    @Override
    public String toString() {
        return "Message{" +
                "attachmentIds=" + attachmentIds +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", channelId=" + channelId +
                '}';
    }
}
