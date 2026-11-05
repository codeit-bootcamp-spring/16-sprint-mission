package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;
import java.util.regex.Pattern;

public record UserUpdateRequest(
        String name,
        String email,
        String password
) {
    public UserUpdateRequest {
        Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("이름은 공백일 수 없습니다.");
        }
        if (email != null && email.isBlank()) {
            throw new IllegalArgumentException("이메일은 공백일 수 없습니다.");
        }
        if (password != null && password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 공백일 수 없습니다.");
        }
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("이메일의 형식이 올바르지 않습니다.");
        }
    }
}
