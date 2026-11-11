package com.sprint.mission.discodeit.dto.userstatus;

import java.util.List;

public record UserStatusListResponse(
        List<UserStatusResponse> userStatuses
) {
}
