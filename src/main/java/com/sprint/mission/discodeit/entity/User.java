package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;

        requiredValue(username, email, password);
    }

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public void updateUsername(String username) {
        this.username = username;
        super.update();
    }

    public void updateEmail(String email) {
        this.email = email;
        super.update();
    }

    public void updatePassword(String password) {
        this.password = password;
        super.update();
    }

    private static void requiredValue(String username, String email, String password) {
        if (username.isBlank() || username == null) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        if (email.isBlank() || email == null) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        if (password.isBlank() || password == null) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }
}
