
package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message extends BaseEntity {

    private String content;
    private UUID userId;
    private UUID channelId;
    private List<UUID> attachmentIds;

    public Message(String content, UUID userId, UUID channelId) {
        this(content, userId, channelId, List.of());
    }

    public Message(String content, UUID userId, UUID channelId,
                   List<UUID> attachmentIds) {
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
        this.attachmentIds = List.copyOf(attachmentIds);
    }

    public List<UUID> getAttachmentIds() {
        return attachmentIds == null
                ? List.of()
                : List.copyOf(attachmentIds);
    }

    public void update(String content) {
        this.content = content;
        updateTimestamp();
    }
}
