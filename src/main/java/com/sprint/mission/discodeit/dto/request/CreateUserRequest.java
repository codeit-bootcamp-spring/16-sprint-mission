package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 생성 요청")
public record CreateUserRequest(
        @Schema(description = "유저명 (중복 불가)", example = "woody")
        String username,
        @Schema(description = "이메일 (중복 불가)", example = "woody@codeit.com")
        String email,
        @Schema(description = "비밀번호", example = "pass1234")
        String password
) {}
