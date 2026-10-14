package com.sprint.mission.discodeit.entity;

public class Message extends Entity {

    private String message;
    private final Channel channel;
    private final User user;

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
