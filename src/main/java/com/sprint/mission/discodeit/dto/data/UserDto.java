package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 조회 응답")
public record UserDto(
        @Schema(description = "사용자 ID")
        UUID id,

        @Schema(description = "사용자 등록 시각", example = "2026-11-16T10:00:00Z")
        Instant createdAt,

        @Schema(description = "사용자 정보가 마지막으로 수정된 시각, 생성 후 초기값 null")
        Instant updatedAt,

        @Schema(description = "사용자 이름", example = "tester")
        String username,

        @Schema(description = "사용자 이메일", example = "tester@example.com")
        String email,

        @Schema(description = "프로필 이미지의 파일 ID. 이미지가 없으면 null")
        UUID profileId,

        @Schema(description = "최근 5분 이내 활동 여부. 사용자 상태 정보가 없으면 null", example = "true")
        Boolean online
) {
}