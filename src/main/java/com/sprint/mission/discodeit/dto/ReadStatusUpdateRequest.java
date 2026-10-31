package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusUpdateRequest {
    private final UUID readStatusId;
    private final Instant lastReadAt;

    public ReadStatusUpdateRequest(UUID readStatusId, Instant lastReadAt) {
        this.readStatusId = readStatusId;
        this.lastReadAt = lastReadAt;
    }
}