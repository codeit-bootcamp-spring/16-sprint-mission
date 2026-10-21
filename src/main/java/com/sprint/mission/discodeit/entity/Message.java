package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {

    private String content;
    private User sender;
    private Channel channel;

    public Message(String content,  User sender, Channel channel) {
        super();
        this.content = content;
        this.sender = sender;
        this.channel = channel;
    }

    public String getContent() { return this.content; }

    public void updateContent(String content) {
        this.update();
        this.content = content;
    }

    public User getSender() { return this.sender; }

    public Channel getChannel() {
        return this.channel;
    }
}
