package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 등록 요청")
public record UserCreateRequest(
        @Schema(description = "등록할 사용자 이름", example = "tester")
        String username,

        @Schema(description = "등록할 이메일", example = "tester@example.com")
        String email,

        @Schema(description = "등록할 비밀번호", format = "password")
        String password
) {
}