package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {

    private String username;

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
}
