package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 접속 상태 수정 요청")
public record UpdateUserStatusRequest(
        @Schema(description = "새 마지막 활동 시각", example = "2026-11-13T00:00:00Z")
        Instant newLastActiveAt
) {
}
