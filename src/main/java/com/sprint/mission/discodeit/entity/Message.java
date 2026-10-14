package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends Base{
    String content;
    private final User user;
    private final Channel channel;

    public Message(String content, User user, Channel channel) {
        this.content = content;
        this.user = user;
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void update(String content) {
        this.content = content;
        updateUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Message{" +
                "content=" + content +
                '}';
    }
}
