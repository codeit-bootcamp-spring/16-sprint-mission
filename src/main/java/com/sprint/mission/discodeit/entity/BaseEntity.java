package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant deletedAt;
    private EntityStatus status;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.status = EntityStatus.NORMAL;
    }

    public void softDelete() {
        this.status = EntityStatus.DELETED;
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return this.status == EntityStatus.DELETED;
    }

    public void validateNotDeleted() {
        if (this.isDeleted()) throw new IllegalStateException("개체가 삭제된 상태입니다.");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;
        BaseEntity entity = (BaseEntity) other;
        return Objects.equals(this.getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public enum EntityStatus {
        NORMAL("정상"),
        DELETED("삭제됨");

        private final String value;

        EntityStatus(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
