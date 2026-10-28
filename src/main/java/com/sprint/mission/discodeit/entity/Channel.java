package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {

    private String channelName;
    private Set<User> users = new HashSet<>();
    private Set<Message> messages = new HashSet<>();

    public Channel(String channelName) {
        super();
        this.channelName = channelName;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void updateChannelName(String channelName) {
        this.update();
        this.channelName = channelName;
    }

    public void addUser(User user) { this.users.add(user); }

    public void removeUser(User user) { this.users.remove(user); }

    public Set<User> getUsers() { return this.users; }
}
