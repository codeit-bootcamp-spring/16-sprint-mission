package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        // @NotNull
        Instant newLastReadAt
) {
}
