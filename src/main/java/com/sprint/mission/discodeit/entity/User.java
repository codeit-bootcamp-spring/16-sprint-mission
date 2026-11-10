
package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import lombok.Getter;

@Getter
public class User extends BaseEntity {

    private String username;
    private String email;
    private String password;
    private UUID profileImageId;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String email, String password, UUID profileImageId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImageId = profileImageId;
    }

    public void update(String username) {
        this.username = username;
        updateTimestamp();
    }

    public void update(String username, String email, String password, UUID profileImageId) {
        if (username != null) {
            this.username = username;
        }

        if (email != null) {
            this.email = email;
        }

        if (password != null) {
            this.password = password;
        }

        if (profileImageId != null) {
            this.profileImageId = profileImageId;
        }

        updateTimestamp();
    }
}
