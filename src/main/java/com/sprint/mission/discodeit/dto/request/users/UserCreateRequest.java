package com.sprint.mission.discodeit.dto.request.users;

import jakarta.validation.constraints.Email; // 이메일 형식 검사
import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지

// Swagger API 문서에서 데이터의 구조와 형식(타입, 설명 등)을 정의하는 어노테이션
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "UserCreateRequest",
        description = "User 생성 정보"
)
public record UserCreateRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String username,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}