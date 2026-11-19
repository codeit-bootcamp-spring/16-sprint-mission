package com.sprint.mission.discodeit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 정보")
public record UserResponse(
        @Schema(description = "사용자 id", example = "11111111-1111-4111-8111-111111111111")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-11-13T00:00:00Z")
        Instant createdAt,
        @Schema(description = "마지막 수정 시각 (수정한 적 없으면 null)", example = "2026-11-13T01:00:00Z")
        Instant updatedAt,
        @Schema(description = "유저명", example = "woody")
        String username,
        @Schema(description = "이메일", example = "woody@codeit.com")
        String email,
        @Schema(description = "비밀번호", example = "pass1234")
        String password,
        @Schema(description = "프로필 이미지(BinaryContent) id, 없으면 null", example = "66666666-6666-4666-8666-666666666666")
        UUID profileId,
        @Schema(description = "현재 접속 여부", example = "true")
        boolean online
) {
    public static UserResponse from(User user, UserStatus status) {
        return new UserResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getPassword(), user.getProfileId(), status.isOnline());
    }
}
