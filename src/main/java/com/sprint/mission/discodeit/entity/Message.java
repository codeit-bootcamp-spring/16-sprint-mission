package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private User sentFrom;
    private String content;
    private Channel channel;

    public Message(User sentFrom, String content, Channel channel) {
        super();
        this.sentFrom = sentFrom;
        this.content = content;
        this.channel = channel;
    }

    public Message update(String content) {
        super.update();
        this.content = content;
        return this;
    }
}
