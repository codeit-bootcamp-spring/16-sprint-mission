package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest( @Schema(description = "읽음 상태를 등록할 사용자 ID") UUID userId,
                                       @Schema(description = "읽음 상태를 등록할 채널 ID") UUID channelId,
                                       @Schema(description = "채널에서 마지막으로 읽은 시각", example = "2026-11-17T09:00:00Z") Instant lastReadAt) {
}
