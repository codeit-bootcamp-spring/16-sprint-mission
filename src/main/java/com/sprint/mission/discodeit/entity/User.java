package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
public class User extends UpdatableEntity {
    private UUID profileId;
    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password) {
        super();
        this.updatedAt = this.getCreatedAt();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User update(String newName, String newEmail, String newPassword) {
        boolean updated = false;

        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            updated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            updated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            updated = true;
        }

        if (updated) {
            this.update();
        }

        return this;
    }

    public void assignProfileImage(UUID profileId) {
        this.profileId = profileId;
        this.update();
    }

    @Override
    public String toString() {
        return "이름: %s, id: %s".formatted(
                this.name,
                this.getId().toString().substring(0, 4));
    }
}
