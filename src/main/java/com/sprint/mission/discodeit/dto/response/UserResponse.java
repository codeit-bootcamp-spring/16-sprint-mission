package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String username,
        String email,
        boolean isOnline
) {
    public static UserResponse from(User user, UserStatus status) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), status.isOnline());
    }
}
