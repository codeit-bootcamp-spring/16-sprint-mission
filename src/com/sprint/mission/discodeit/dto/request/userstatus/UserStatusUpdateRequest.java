package com.sprint.mission.discodeit.dto.request.userstatus;

import java.time.Instant;

public record UserStatusUpdateRequest(
        Instant newLastActiveAt
) {
}