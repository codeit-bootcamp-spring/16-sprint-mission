package com.sprint.mission.discodeit.dto.request.auth;

import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지

public record AuthLoginRequest(

        @NotBlank(message = "사용자 이름은 필수입니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}