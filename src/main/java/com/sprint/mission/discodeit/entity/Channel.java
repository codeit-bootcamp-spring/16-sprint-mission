package com.sprint.mission.discodeit.entity;

import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {

    private String channelName;
    private Set<User> users;
    private Set<Message> messages;

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

    public void addUser(User user) {
        this.users.add(user);
    }
}
