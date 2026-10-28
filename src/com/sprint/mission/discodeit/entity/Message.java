package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String content;
    private final UUID authorId;
    private final UUID channelId;

    public Message(String content, UUID authorId, UUID channelId) {
        super();
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void update(String content) {
        this.content = content;
        updateUpdatedAt();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Message message = (Message) obj;
        return getId().equals(message.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
