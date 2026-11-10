package com.sprint.mission.discodeit.entity;

public class Validation {

    private static final String EMAIL_REGEX =
            "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,}$";

    // 이메일 형식 검증
    public static void validateEmailRegex(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("이메일 형식이 유효하지 않습니다.");
        }
    }

    // null 또는 공백 검증
    public static void validateNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + "이(가) null입니다.");
        }
    }

    public static void validateBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "을(를) 작성해 주세요.");
        }
    }

    public static void validateWhitespace(String value, String name) {
        if (value == null || value.isBlank()
                || value.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException(name + "은(는) null이거나 공백을 포함할 수 없습니다.");
        }
    }
}
