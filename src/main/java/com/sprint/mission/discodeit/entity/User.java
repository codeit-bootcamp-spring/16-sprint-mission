package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {

    private String userName;

    public User(String userName) {
        super();
        this.userName = userName;
    }

    public String getName() {
        return userName;
    }

    public void updateUserName(String userName) {
        this.update();
        this.userName = userName;
    }
}
