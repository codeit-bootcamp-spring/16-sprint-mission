package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record UserCreateResponse(
        UUID id,
        String name,
        String email,
        UUID profileId
) {
    public static UserCreateResponse from(User user) {
        return new UserCreateResponse(user.getId(), user.getName(), user.getEmail(), user.getProfileId());
    }
}
