package com.sprint.mission.discodeit.dto.userStatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateResponse(
        UUID id,
        UUID userId,
        Instant lastActiveTime
) {
    public static UserStatusUpdateResponse from(UserStatus userStatus) {
        return new UserStatusUpdateResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastActiveTime()
        );
    }
}
