package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class UpdatableEntity extends BaseEntity {
    protected Instant updatedAt;

    protected void update() {
        this.updatedAt = Instant.now();
    }

    @Override
    public void softDelete() {
        super.softDelete();
        this.update();
    }
}
