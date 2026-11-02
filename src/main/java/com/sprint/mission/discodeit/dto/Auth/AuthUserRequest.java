package com.sprint.mission.discodeit.dto.Auth;

public record AuthUserRequest(
        String name,
        String password
) {
    public AuthUserRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 공백일 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 공백일 수 없습니다.");
        }
    }
}
