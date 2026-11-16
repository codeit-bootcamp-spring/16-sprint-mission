package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @Schema(description = "유저명", example = "woody")
        String username,
        @Schema(description = "비밀번호", example = "pass1234")
        String password
) {}
