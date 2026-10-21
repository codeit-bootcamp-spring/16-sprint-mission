package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String content;
    private final User sender;
    private final Channel channel;

    public Message(String content, User sender, Channel channel) {
        super();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지를 작성해 주세요.");
        }
        Validation.validateNull(sender, "sender");
        Validation.validateNull(channel, "channel");
        this.content = content;
        this.sender = sender;
        this.channel = channel;
    }

    public String getContent() { return content; }
    public User getSender() { return sender; }
    public Channel getChannel() { return channel; }

    public void updateMessage(String content) {
        Validation.validateBlank(content, "message");
        this.content = content;
        super.update();
    }
}
