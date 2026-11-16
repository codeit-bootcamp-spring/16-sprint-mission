package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 수정 요청")
public record UpdateUserRequest(
        @Schema(description = "새 유저명 (중복 불가)", example = "buzz")
        String newUsername,
        @Schema(description = "새 이메일 (중복 불가)", example = "buzz@codeit.com")
        String newEmail,
        @Schema(description = "새 비밀번호", example = "newpass1234")
        String newPassword
) {}
