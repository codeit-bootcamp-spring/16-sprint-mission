
package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    protected void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}
