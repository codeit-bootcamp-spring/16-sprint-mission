package com.sprint.mission.discodeit.entity;

import java.util.Set;

public class User extends BaseEntity {

    private String username;
    private Set<Message> messages;
    private Set<Channel> channels;

    public User(String username) {
        super();
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void updateUsername(String username) {
        this.update();
        this.username = username;
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }
}
