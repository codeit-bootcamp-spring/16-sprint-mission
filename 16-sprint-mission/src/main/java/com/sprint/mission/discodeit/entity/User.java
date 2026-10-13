package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String password;
    private Long createdAt;
    private Long updatedAt;

    public User(String name, String password) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.password = password;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getPassword() {
        return this.password;
    }
    public Long getCreatedAt() {
        return this.createdAt;
    }
    public Long getUpdatedAt() {
        return this.updatedAt;
    }

    public void update(String name, String password) {
        this.name = name;
        this.password = password;
        this.updatedAt = Instant.now().getEpochSecond();
    }

}
