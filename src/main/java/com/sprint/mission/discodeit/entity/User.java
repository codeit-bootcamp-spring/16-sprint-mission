package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {

    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void update(String username) {
        this.username = username;
        updateTimestamp();
    }
}