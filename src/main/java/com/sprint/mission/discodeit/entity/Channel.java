package com.sprint.mission.discodeit.entity;

import java.util.List;

public class Channel extends BaseEntity {
    private String name;
    private List<User> users;
    private List<Message> messages;

    public Channel(String name) {
        super();
        this.name = name;
    }

    public Channel update(String name) {
        super.update();
        this.name = name;
        return this;
    }
}
