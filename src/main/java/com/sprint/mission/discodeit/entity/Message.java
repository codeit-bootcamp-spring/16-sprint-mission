package com.sprint.mission.discodeit.entity;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private final UUID channelId;
    private final UUID senduser;
    private String content;
    private boolean bookmarked;


public Message(UUID channelId, UUID senduser, String content) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.channelId = channelId;
    this.senduser = senduser;
    this.content = content;
    this.bookmarked = false;
}

public UUID getId() {
    return id;
}
public Long getCreatedAt() {
    return createdAt;
}
public Long getUpdatedAt() {
    return updatedAt;
}
public UUID getChannelId() {
    return channelId;
}
public UUID getSenduser() {
    return senduser;
}
public String getContent() {
    return content;
}

public boolean isBookmarked() {
    return bookmarked;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
    public void toggleBookmark() {
        this.bookmarked = !this.bookmarked;
        this.updatedAt = System.currentTimeMillis();
        }
}
