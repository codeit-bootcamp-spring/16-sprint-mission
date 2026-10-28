package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        updatedAt = this.createdAt;
    }

    public void update() {
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        BaseEntity that = (BaseEntity) object;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}