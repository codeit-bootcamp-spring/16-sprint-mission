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

    public void validateNotDeleted() {
        if (this.isDeleted()) throw new IllegalStateException("삭제된 메세지입니다.");
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
        return "id: %s, 보낸 사람: %s, 내용: %s, 채널: %s, 상태: %s".formatted(
                this.getId().toString().substring(0, 4),
                this.sentFrom.getName(), this.content,
                this.channel.getName(), super.getStatus());
    }
}
