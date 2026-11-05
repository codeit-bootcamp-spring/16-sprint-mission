package com.sprint.mission.discodeit.dto.auth;

public record AuthUserRequest(
        String email,
        String password
) {
    public AuthUserRequest {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이름은 공백일 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 공백일 수 없습니다.");
        }
    }
}
