package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        boolean isOnline
) {
    public static UserResponse from(User user, UserStatus status) {
        return new UserResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getProfileId(), status.isOnline());
    }
}
