package com.sprint.mission.discodeit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidation implements ConstraintValidator<Username, String> {
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return false;
        } else if (!username.matches("[a-z0-9._]{2,32}")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "사용자 이름은 2~32자여야 하며, 영문 소문자, 숫자, 마침표(.), 밑줄(_)만 사용할 수 있습니다."
            ).addConstraintViolation();

            return false;
        } else if (username.contains("..")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "사용자 이름에는 연속된 마침표(..)를 사용할 수 없습니다."
            ).addConstraintViolation();

            return false;
        } else {
            return true;
        }
    }
}
