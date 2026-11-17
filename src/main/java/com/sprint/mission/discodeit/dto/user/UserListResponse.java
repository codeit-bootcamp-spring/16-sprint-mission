package com.sprint.mission.discodeit.dto.user;

import java.util.List;

public record UserListResponse(
        List<UserResponse> users
) {
}
