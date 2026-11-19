package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "읽음 상태 생성 요청")
public record CreateReadStatusRequest(
        @Schema(description = "사용자 id", example = "11111111-1111-4111-8111-111111111111")
        UUID userId,
        @Schema(description = "채널 id", example = "22222222-2222-4222-8222-222222222222")
        UUID channelId,
        @Schema(description = "마지막으로 읽은 시각", example = "2026-11-13T00:00:00Z")
        Instant lastReadAt
) {
}
