package com.sprint.mission.discodeit.dto.userStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserStatusUpdateRequest(
        @Schema(example = "2026-11-16T05:34:27.510Z")
        @NotNull
        Instant newLastActiveAt
) {
}
