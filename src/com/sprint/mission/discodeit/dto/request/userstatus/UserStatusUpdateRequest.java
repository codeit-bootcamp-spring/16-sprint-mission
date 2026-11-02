package com.sprint.mission.discodeit.dto.request.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID id,
        Instant lastActiveAt
) {
}