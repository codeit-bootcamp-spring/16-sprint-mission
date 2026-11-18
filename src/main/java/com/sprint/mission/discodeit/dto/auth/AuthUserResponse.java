package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record AuthUserResponse(
        UUID id,
        String name,
        String email,
        UUID profileId
) {
    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(user.getId(), user.getName(), user.getEmail(), user.getProfileId());
    }
}
