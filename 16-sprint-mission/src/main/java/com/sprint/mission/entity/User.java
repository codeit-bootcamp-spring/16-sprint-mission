package com.sprint.mission.entity;

public class User extends Entity {

    private String userName;
    private String email;
    private String password;

    public User(String createdAt, String updatedAt, String userName, String email, String password) {
        super(createdAt, updatedAt);
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
