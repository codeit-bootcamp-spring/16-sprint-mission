package com.sprint.mission.discodeit.request.users;

import jakarta.validation.constraints.Email; // 이메일 형식 검사
import jakarta.validation.constraints.Pattern; // 지정한 정규표현식에 맞는지 검사

// Swagger API 문서에서 데이터의 구조와 형식(타입, 설명 등)을 정의하는 어노테이션
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "UserUpdateRequest",
        description = "수정할 User 정보"
)
public record UserUpdateRequest(

        @Pattern(
                regexp = "^\\S+$",
                message = "사용자 이름에는 공백을 포함할 수 없습니다.")
        String newUsername,

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Pattern(
                regexp = "^\\S+$",
                message = "이메일에는 공백을 포함할 수 없습니다.")
        String newEmail,

        @Pattern(
                regexp = "^\\S+$",
                message = "비밀번호는 공백을 포함할 수 없습니다.")
        String newPassword
) {
}