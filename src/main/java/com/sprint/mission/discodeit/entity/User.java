package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class User extends BaseEntity{
    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;

    private UUID profileId;

    public User(String username, String email, String password) {
        this(username, email, password, null);
    }

    public User(String username, String email, String password, UUID profileId) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(
            String username,
            String email,
            String password,
            UUID profileId
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
        updateUpdatedAt();
    }

}
