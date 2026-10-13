package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Long getCreatedAt() {
        return this.createdAt;
    }

    public Long getUpdatedAt() {
        return this.updatedAt;
    }

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = this.createdAt;
    }


    public void update(String name) {
        this.name = name;
        this.updatedAt = Instant.now().getEpochSecond();
    }

}


