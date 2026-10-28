package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class Message extends Entity implements Serializable {

    private String message;
    private final Channel channel;
    private final User user;

    private static final long serialVersionUID = 2;

    public Message(String message, Channel channel, User user) {
        this.message = message;
        this.channel = channel;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void update(String message) {
        if (message != null) {
            this.message = message;
        }
        touch();
    }

    // 다른 복사본(같은 id)의 최신 정보를 이 객체에 반영한다.
    public void sync(Message source) {
        this.message = source.message;
        this.updatedAt = source.updatedAt;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }
}
