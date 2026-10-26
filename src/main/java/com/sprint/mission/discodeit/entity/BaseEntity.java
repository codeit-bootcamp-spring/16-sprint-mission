package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity implements java.io.Serializable {

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    // 객체를 생성할 때 공통 필드 초기화
    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    // ID 조회
    public UUID getId() {
        return id;
    }

    // 생성 시간 조회
    public Long getCreatedAt() {
        return createdAt;
    }

    // 수정 시간 조회
    public Long getUpdatedAt() {
        return updatedAt;
    }

    // 수정 시간이 현재 시간으로 바뀌도록 함
    protected void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
