package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        updatedAt = this.createdAt;
    }

    public void update() {
        this.updatedAt = Instant.now();
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