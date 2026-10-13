package com.sprint.mission.discodeit.entity;

public class User extends Message {

    private String name;

    public User(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
