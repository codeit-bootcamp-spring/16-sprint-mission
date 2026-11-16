package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
        @NotNull
        UUID userId,
        @NotNull
        UUID channelId,
        // @NonNull
        Instant lastReadAt
) {
}
