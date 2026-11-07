package com.sprint.mission.discodeit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidation implements ConstraintValidator<Password, String> {
    @Override
    // context: 검증 실패 메시지를 직접 설정하거나, 어떤 필드에서 검증에 실패했는지 등의 정보를 구성하는 데 사용하는 객체입니다.
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean hasLetter = password.matches(".*[A-Za-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[^A-Za-z0-9\\s].*");

        if (password.length() < 12) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호는 최소 12자 이상이여야 합니다."
            ).addConstraintViolation();

            return false;
        } else if (password.length() > 64) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호는 64자를 초과할 수 없습니다."
            ).addConstraintViolation();
            return false;
        } else if (!hasLetter) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호에는 영문 알파벳이 최소 1개 이상 포함되어 있어야합니다."
            ).addConstraintViolation();

            return false;
        } else if (!hasDigit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호에는 숫자가 최소 1개 이상 포함되어 있어야합니다."
            ).addConstraintViolation();

            return false;
        } else if (!hasSpecial) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호에는 영문, 숫자, 공백이 아닌 문자가 최소 1개 이상 포함되어 있어야합니다."
            ).addConstraintViolation();

            return false;
        } else if (password.matches(".*\\s.*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "비밀번호에는 공백이 포함되어있으면 안됩니다."
            ).addConstraintViolation();

            return false;
        } else {
            return true;
        }

    }
}
