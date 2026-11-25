package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 접속 상태 정보")
public record UserStatusResponse(
        @Schema(description = "접속 상태 id", example = "55555555-5555-4555-8555-555555555555")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-11-13T00:00:00Z")
        String createdAt,
        @Schema(description = "마지막 수정 시각", example = "2026-11-13T01:00:00Z")
        String updatedAt,
        @Schema(description = "사용자 id", example = "11111111-1111-4111-8111-111111111111")
        UUID userId,
        @Schema(description = "마지막 활동 시각", example = "2026-11-13T00:00:00Z")
        String lastActiveAt,
        @Schema(description = "현재 접속 여부", example = "true")
        boolean online
) {

    public static UserStatusResponse from(UserStatus status) {
        return new UserStatusResponse(status.getId(), status.getCreatedAt().toString(),
                status.getUpdatedAt().toString(), status.getUserId(), status.getLastActiveAt().toString(),
                status.isOnline());
    }
}
