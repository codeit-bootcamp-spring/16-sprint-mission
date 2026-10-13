package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    // 필드
    UUID id;
    Long createdAt;
    Long updatedAt;

    // 생성자 초기화
    public Channel(UUID id, Long createdAt, Long updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter 함수 정의
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
}
