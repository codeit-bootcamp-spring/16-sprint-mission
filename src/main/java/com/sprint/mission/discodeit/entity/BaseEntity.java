package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // 필드를 private으로 선언해 외부에서 직접 수정하지 못하게 하고,
    // Getter로 값을 읽을 수 있게 setting.
    // 불변 객체로 선언해서 외부 접근 및 값 변경 방지
    private final UUID id; // 고유 식별자
    private final Long createdAt;
    private Long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID(); // 객체를 생성할 때 새로운 식별자를 발급
        this.createdAt = System.currentTimeMillis(); // 밀리초 단위
        this.updatedAt = this.createdAt; // 처음 생성됐을 때 생성 시간과 수정 시간이 같도록 설정
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    protected void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }

}
