package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
        @NotNull
        @Schema(
                description = "읽음 상태를 생성할 사용자 UUID",
                example = "550e8400-e29b-41d4-a716-446655440001"
        )
        UUID userId,

        @NotNull
        @Schema(
                description = "읽음 상태를 생성할 채널 UUID",
                example = "550e8400-e29b-41d4-a716-446655440002"
        )
        UUID channelId,

        @Schema(
                description = "마지막 읽음 시각. 생성 시 전달값은 사용하지 않고 서버 시각으로 초기화됩니다.",
                example = "2026-11-17T06:00:00Z"
        )
        Instant lastReadAt
) {
}
