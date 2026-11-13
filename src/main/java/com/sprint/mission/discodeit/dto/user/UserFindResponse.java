package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record UserFindResponse(
        UUID id,
        String name,
        String email,
        UUID profileId,
        boolean isOnline
) {
    public static UserFindResponse of(User user, boolean isOnline) {
        return new UserFindResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileId(),
                isOnline
        );
    }
}
