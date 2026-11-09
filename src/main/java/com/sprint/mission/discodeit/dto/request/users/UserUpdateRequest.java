package com.sprint.mission.discodeit.dto.request.users;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;
import jakarta.validation.constraints.Email; // 이메일 형식 검사
import jakarta.validation.constraints.NotNull; // null 값 방지
import jakarta.validation.constraints.Pattern; // 지정한 정규표현식에 맞는지 검사

import java.util.UUID;

public record UserUpdateRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @Pattern(
                regexp = "^\\S+$",
                message = "사용자 이름에는 공백을 포함할 수 없습니다.")
        String username,

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Pattern(
                regexp = "^\\S+$",
                message = "이메일에는 공백을 포함할 수 없습니다.")
        String email,

        @Pattern(
                regexp = "^\\S+$",
                message = "비밀번호는 공백을 포함할 수 없습니다.")
        String password,

        BinaryContentCreateRequest profile) { }