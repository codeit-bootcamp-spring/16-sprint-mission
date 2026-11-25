package com.sprint.mission.discodeit.dto.request.readstatus;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        Instant newLastReadAt
) {
}