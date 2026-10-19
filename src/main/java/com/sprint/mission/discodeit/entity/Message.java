package com.sprint.mission.discodeit.entity;
import java.util.UUID;

public class Message extends BaseEntity {

    private final UUID channelId;
    private final UUID senduser;
    private String content;
    private boolean bookmarked;


public Message(UUID channelId, UUID senduser, String content) {
    super();
    this.channelId = channelId;
    this.senduser = senduser;
    this.content = content;
    this.bookmarked = false;
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
        updateTimestamp();
    }
    public void toggleBookmark() {
        this.bookmarked = !this.bookmarked;
        updateTimestamp();
        }
}
