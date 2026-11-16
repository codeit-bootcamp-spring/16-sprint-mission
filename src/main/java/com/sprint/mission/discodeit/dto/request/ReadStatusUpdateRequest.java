package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마지막 읽음 시각 수정 요청")
public record ReadStatusUpdateRequest(
        @Schema(description = "새 마지막 읽음 시각", example = "2026-11-16T10:10:00Z")
        Instant newLastReadAt
) {
}