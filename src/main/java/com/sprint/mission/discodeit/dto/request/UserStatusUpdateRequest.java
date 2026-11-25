package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자의 마지막 활동 시각 갱신 요청")
public record UserStatusUpdateRequest(
        @Schema(
                description = "새 마지막 활동 시각",
                example = "2026-11-16T10:00:00Z"
        )
        Instant newLastActiveAt
) {
}