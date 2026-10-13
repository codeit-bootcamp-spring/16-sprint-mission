package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        super();

        Validation.validateBlank(username, "username");
        Validation.validateBlank(password, "password");
        if (email != null) {
            Validation.validateBlank(email, "email");
            Validation.validateEmailRegex(email);
        }

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
        Validation.validateBlank(username, "username");

        this.username = username;
        super.update();
    }

    public void changePassword(String password) {
        Validation.validateBlank(password, "password");

        this.password = password;
        super.update();
    }

    public void changeEmail(String email) {
        Validation.validateBlank(email, "email");
        Validation.validateEmailRegex(email);

        this.email = email;
        super.update();
    }

    public void deleteEmail() {
        this.email = null;
        super.update();
    }

}