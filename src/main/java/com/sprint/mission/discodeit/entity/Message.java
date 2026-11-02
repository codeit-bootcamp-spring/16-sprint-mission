package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String comment;
    private UUID authorId;
    private UUID channelId;
    private List<UUID> attachmentIds;

    public Message(String comment,UUID authorId, UUID channelId, List<UUID> attachmentIds) {

        this.comment = comment;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachmentIds = List.copyOf(attachmentIds);
    }

    public List<UUID> getAttachmentIds() {
        return List.copyOf(attachmentIds);
    }

    public void update(String comment) {
        this.comment = comment;
        updateTime();
    }

}
