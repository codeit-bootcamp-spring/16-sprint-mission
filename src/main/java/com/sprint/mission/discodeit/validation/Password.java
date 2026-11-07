package com.sprint.mission.discodeit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Java에서 애너테이션을 직접 정의할 때 사용하는 문법입니다.
@Constraint(validatedBy = PasswordValidation.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "유효하지 않은 비밀번호입니다.";

    Class<?>[] groups() default {}; // 커스텀 애너테이션에 필요한 필수요소입니다. 지금은 몰라도됩니다.

    Class<? extends Payload>[] payload() default {}; // 커스텀 애너테이션에 필요한 필수요소입니다. 지금은 몰라도됩니다.
}
