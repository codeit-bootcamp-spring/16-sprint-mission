package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusUpdateRequest {
    private final UUID userStatusId;
    private final Instant lastActiveAt;

    public UserStatusUpdateRequest(UUID userStatusId, Instant lastActiveAt) {
        this.userStatusId = userStatusId;
        this.lastActiveAt = lastActiveAt;
    }
}