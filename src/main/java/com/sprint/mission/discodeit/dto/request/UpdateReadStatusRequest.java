package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "읽음 상태 수정 요청")
public record UpdateReadStatusRequest(
        @Schema(description = "새 마지막으로 읽은 시각 (현재보다 미래일 수 없음)", example = "2026-11-13T01:00:00Z")
        Instant newLastReadAt
) {}
