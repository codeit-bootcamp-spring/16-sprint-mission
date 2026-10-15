package discodeit.entity;

import java.util.UUID;

public class Common {

    protected UUID id;
    protected Long createdAt;
    protected Long updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Common(Long createdAt) {
        this.id = UUID.randomUUID();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
