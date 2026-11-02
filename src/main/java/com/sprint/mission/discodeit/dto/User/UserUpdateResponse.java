package com.sprint.mission.discodeit.dto.User;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record UserUpdateResponse(
        UUID id,
        String name,
        String email,
        UUID profileId
) {
    public static UserUpdateResponse from(User user) {
        return new UserUpdateResponse(user.getId(), user.getName(), user.getEmail(), user.getProfileId());
    }
}
