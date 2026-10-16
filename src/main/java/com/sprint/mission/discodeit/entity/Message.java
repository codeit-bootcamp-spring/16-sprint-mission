package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private final User sentFrom;
    private final Channel channel;
    private String content;

    public Message(User sentFrom, String content, Channel channel) {
        super();
        this.sentFrom = sentFrom;
        this.content = content;
        this.channel = channel;
    }

    public Message updateContent(String content) {
        this.content = content;
        super.update();
        return this;
    }

    public User getSentFrom() {
        return this.sentFrom;
    }

    public String getContent() {
        return this.content;
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
