package com.sprint.mission.discodeit.dto.UserStatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusFindResponse(
        UUID id,
        UUID userId,
        Instant lastActiveTime
) {
    public static UserStatusFindResponse from(UserStatus userStatus) {
        return new UserStatusFindResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastActiveTime()
        );
    }
}
