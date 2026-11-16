package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "읽음 상태 정보")
public record ReadStatusResponse(
        @Schema(description = "읽음 상태 id", example = "44444444-4444-4444-8444-444444444444")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-11-13T00:00:00Z")
        String createdAt,
        @Schema(description = "마지막 수정 시각", example = "2026-11-13T01:00:00Z")
        String updatedAt,
        @Schema(description = "사용자 id", example = "11111111-1111-4111-8111-111111111111")
        UUID userId,
        @Schema(description = "채널 id", example = "22222222-2222-4222-8222-222222222222")
        UUID channelId,
        @Schema(description = "마지막으로 읽은 시각", example = "2026-11-13T00:00:00Z")
        String lastReadAt
) {

    public static ReadStatusResponse from(ReadStatus status) {
        return new ReadStatusResponse(status.getId(), status.getCreatedAt().toString(),
                status.getUpdatedAt().toString(), status.getUserId(),
                status.getChannelId(), status.getLastReadAt().toString());
    }
}
