package com.sprint.mission.discodeit.dto.userStatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateResponse(
        UUID id,
        UUID userId,
        Instant lastActiveTime
) {
    public static UserStatusCreateResponse from(UserStatus userStatus) {
        return new UserStatusCreateResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastActiveTime()
        );
    }
}
