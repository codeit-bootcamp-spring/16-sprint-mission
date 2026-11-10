package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
        UUID statusId,
        UUID userId,
        Instant lastActiveAt
) {

    public static UserStatusResponse from(UserStatus status) {
        return new UserStatusResponse(status.getId(), status.getUserId(), status.getLastActiveAt());
    }
}
