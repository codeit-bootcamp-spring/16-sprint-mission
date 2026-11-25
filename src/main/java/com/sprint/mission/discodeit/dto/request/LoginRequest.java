package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @Schema(description = "가입할 때 사용한 사용자 이름", example = "tester")
        String username,

        @Schema(description = "사용자 비밀번호", format = "password")
        String password
) {
}