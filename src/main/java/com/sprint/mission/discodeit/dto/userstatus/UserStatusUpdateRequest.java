package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record UserStatusUpdateRequest(
        @Schema(
                description = "마지막 활동 시각. 현재 구현에서는 전달값을 사용하지 않고 서버 시각으로 갱신합니다.",
                example = "2026-11-17T06:00:00Z"
        )
        Instant newLastActiveAt
) {
}
