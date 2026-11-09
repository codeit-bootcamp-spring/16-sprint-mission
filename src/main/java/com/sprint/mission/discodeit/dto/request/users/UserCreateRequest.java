package com.sprint.mission.discodeit.dto.request.users;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;
import jakarta.validation.constraints.Email; // 이메일 형식 검사
import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지

public record UserCreateRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String username,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        BinaryContentCreateRequest profile) { }