package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

@Getter
public class User extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private String username;
    private String password;
    private String email;
    private UUID profileId;

    public User(String username, String password, String email) {
        super();

        Validation.validateWhitespace(username, "username");
        Validation.validateWhitespace(password, "password");
        if (email != null) {
            Validation.validateWhitespace(email, "email");
            Validation.validateEmailRegex(email);
        }

        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void changeUsername(String username) {
        Validation.validateWhitespace(username, "username");
        this.username = username;
        super.update();
    }

    public void changePassword(String password) {
        Validation.validateWhitespace(password, "password");
        this.password = password;
        super.update();
    }

    public void changeEmail(String email) {
        Validation.validateWhitespace(email, "email");
        Validation.validateEmailRegex(email);
        this.email = email;
        super.update();
    }

    public void changeProfileId(UUID profileId) {
        Validation.validateNull(profileId, "profileId");
        this.profileId = profileId;
        super.update();
    }

    public void deleteEmail() {
        if (this.email != null) {
            this.email = null;
            super.update();
        }
    }

    public void deleteProfileId() {
        if (this.profileId != null) {
            this.profileId = null;
            super.update();
        }
    }
}