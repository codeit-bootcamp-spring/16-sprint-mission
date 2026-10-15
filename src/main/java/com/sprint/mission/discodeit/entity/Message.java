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

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }
}
