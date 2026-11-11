package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class User extends Base {
    private UUID profileId;
    private String username;
    private int age;
    private final String email;
    private String password;

    public void update(String username, Integer age, String password) {
        if (username != null)
            this.username = username;
        if (age != null)
            this.age = age;
        if (password != null)
            this.password = password;
        updateUpdatedAt();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        updateUpdatedAt();
    }

    @Override
    public String toString() {
        return "User {" +
                "name='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
