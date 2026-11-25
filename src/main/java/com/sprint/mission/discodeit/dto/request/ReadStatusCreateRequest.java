package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자와 채널의 읽음 상태 등록 요청")
public record ReadStatusCreateRequest(
        @Schema(description = "읽음 상태를 등록할 사용자의 UUID")
        UUID userId,

        @Schema(description = "읽음 상태를 등록할 채널의 UUID")
        UUID channelId,

        @Schema(description = "채널을 마지막으로 읽은 시각", example = "2026-11-16T10:00:00Z")
        Instant lastReadAt
) {
}