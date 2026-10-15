package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class BaseEntity {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    public BaseEntity(){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = this.createdAt;
    }
    public UUID getId() {
        return this.id;
    }
    public Long getCreatedAt() {
        return this.createdAt;
    }
    public Long getUpdatedAt() {
        return this.updatedAt;
    }

    protected void updateTime(){
        this.updatedAt = Instant.now().getEpochSecond();
    }
}

