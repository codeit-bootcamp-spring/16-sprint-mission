package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private User sentFrom;
    private Channel channel;
    private String content;

    public Message(User sentFrom, String content, Channel channel) {
        super();
        this.sentFrom = sentFrom;
        this.content = content;
        this.channel = channel;
    }

    public Message update(Message message) {
        this.sentFrom = message.sentFrom;
        this.content = message.content;
        this.channel = message.channel;
        super.update();
        return this;
    }

    public User getSentFrom() {
        return this.sentFrom;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public String toString() {
        return "보낸 사람: %s, 내용: %s, 채널: %s, 상태: %s".formatted(this.sentFrom.getName(), this.content,
                this.channel.getName(), super.getStatus());
    }
}
