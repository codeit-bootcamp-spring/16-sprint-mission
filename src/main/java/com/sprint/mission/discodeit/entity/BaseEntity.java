package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {

    private UUID id;
    private Long createAt;
    private Long updateAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void update() {
        this.updateAt = System.currentTimeMillis();
    }

}
