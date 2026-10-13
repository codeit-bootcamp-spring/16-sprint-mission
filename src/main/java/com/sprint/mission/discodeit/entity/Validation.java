package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Validation {
    private static final String EMAIL_REGEX =
            "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$";

    // null 또는 공백을 포함하는지 검증
    public static void validateBlank(String value, String name) {
        if (value == null || value.isEmpty()
            || value.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException(name + "은(는) null이거나 공백을 포함할 수 없습니다.");
        }
    }

    public static void validateBlank(UUID value, String name) {
        if (value == null || value.toString().isEmpty()) {
            throw new IllegalArgumentException(name + "는 null이 불가능합니다.");
        }
    }

    // 이메일 형식 검증
    public static void validateEmailRegex(String value) {
        if (!value.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("이메일 형식이 유효하지 않습니다.");
        }
    }

}
