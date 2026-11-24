package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();

        Validation.validateNull(userId, "userId");
        Validation.validateNull(channelId, "channelId");

        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt != null ? lastReadAt : Instant.now();
    }

    public void updateLastReadAt(Instant newLastReadAt) {
        this.lastReadAt = newLastReadAt;
        super.update();
    }
}
