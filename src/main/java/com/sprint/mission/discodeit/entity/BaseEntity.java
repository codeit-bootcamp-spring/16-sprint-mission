package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private Long deletedAt;
    private EntityStatus status;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = this.createdAt;
        this.status = EntityStatus.ACTIVE;
    }

    public UUID getId() {
        return this.id;
    }

    protected void update() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

    public void softDelete() {
        this.status = EntityStatus.DELETED;
        this.deletedAt = Instant.now().toEpochMilli();
    }

    protected void updateToDeleted(Long deletedAt) {
        this.deletedAt = deletedAt;
        this.status = EntityStatus.DELETED;
    }

    public Long getCreatedAt() {
        return this.createdAt;
    }

    public Long getUpdatedAt() {
        return this.updatedAt;
    }

    public Long getDeleteAt() {
        return this.deletedAt;
    }

    public EntityStatus getStatus() {
        return this.status;
    }

    public boolean isDeleted() {
        return this.status == EntityStatus.DELETED;
    }

    public enum EntityStatus {
        ACTIVE("활동 중"),
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
