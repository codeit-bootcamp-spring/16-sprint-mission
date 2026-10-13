package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void changeUsername(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        this.username = username;
        super.update();
    }

    public void changePassword(String password) {
        if (password == null || password.isBlank()) {
            return;
        }
        this.password = password;
        super.update();
    }

    public void changeEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        this.email = email;
        super.update();
    }

}
