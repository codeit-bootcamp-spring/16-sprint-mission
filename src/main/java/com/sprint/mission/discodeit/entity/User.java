package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;

public class User extends BaseEntity {

    private String username;
    private Set<Message> messages = new HashSet<>();
    private Set<Channel> channels = new HashSet<>();

    public User(String username) {
        super();
        this.username = username;
    }

    public String getUsername() { return this.username; }

    public void updateUsername(String username) {
        this.update();
        this.username = username;
    }

    public void addChannel(Channel channel) { this.channels.add(channel); }

    public void removeChannel(Channel channel) { this.channels.remove(channel); }
}
