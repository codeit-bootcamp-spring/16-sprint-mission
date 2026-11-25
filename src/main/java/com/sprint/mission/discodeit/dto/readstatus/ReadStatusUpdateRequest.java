package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        @Schema(
                description = "변경할 마지막 읽음 시각",
                example = "2026-11-17T07:00:00Z"
        )
        Instant newLastReadAt
) {
}
