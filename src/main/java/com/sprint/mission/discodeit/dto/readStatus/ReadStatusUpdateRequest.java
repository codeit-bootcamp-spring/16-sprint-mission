package com.sprint.mission.discodeit.dto.readStatus;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        @NotBlank
        Instant lastReadTime
) {
}
