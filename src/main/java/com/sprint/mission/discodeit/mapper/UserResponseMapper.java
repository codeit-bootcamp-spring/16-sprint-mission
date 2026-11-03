
package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

public class UserResponseMapper {

    private UserResponseMapper() {
    }

    public static UserResponse toResponse(User user, UserStatus userStatus) {
        boolean online = userStatus != null && userStatus.isOnline();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                online
        );
    }
}
