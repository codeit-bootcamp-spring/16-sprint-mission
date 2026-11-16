package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 정보 수정 요청. 변경할 필드만 전달")
public record UserUpdateRequest(
        @Schema(description = "새 사용자 이름", example = "tester02")
        String newUsername,

        @Schema(description = "새 이메일", example = "tester02@example.com")
        String newEmail,

        @Schema(description = "새 비밀번호", format = "password")
        String newPassword
) {
}