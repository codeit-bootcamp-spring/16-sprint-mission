package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusDto {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final UUID userId;
    private final UUID channelId;
    private final Instant lastReadAt;

    public ReadStatusDto(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
}
