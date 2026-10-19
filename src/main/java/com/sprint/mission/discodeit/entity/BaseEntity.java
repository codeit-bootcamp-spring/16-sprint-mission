package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {
    UUID id;
    Long createdAt;
    Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        long currentTime = System.currentTimeMillis();
        this.createdAt = currentTime;
        this.updatedAt = currentTime; // 생성 시점에는 수정일도 생성일과 동일
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    // 수정 시간을 갱신
    protected void updateTime() {
        this.updatedAt = System.currentTimeMillis();
    }
}

