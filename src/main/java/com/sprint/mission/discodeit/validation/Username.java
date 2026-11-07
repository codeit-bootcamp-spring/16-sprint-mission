package com.sprint.mission.discodeit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @Constraint 애너테이션을 사용하여 실제 검증 로직을 구현한 클래스를 지정합니다.
@Constraint(validatedBy = UsernameValidation.class)
// 커스텀 애너테이션을 적용할 수 있는 위치를 지정합니다.
// 여기서는 필드, 파라미터, 레코드의 컴포넌트에 적용할 수 있고,
// 다른 애터네이션을 선언할 때 해당 애너테이션이 적용될 수 있습니다.
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT, ElementType.ANNOTATION_TYPE})
// @Retention은 애너테이션 정보가 언제까지 유지되는지 지정하는 메타 애너테이션입니다.
// @Retention(RetentionPolicy.RUNTIME) 는 해당 애너테이션이 런타임중에 사용된 다는 것을 의미합니다.
// 구체적으로, Jakarta Validation의 구현체인 Hibernate Validator가 프로그램 실행 중에 @Username 애너테이션을 확인하고,
// 연결된 UsernameValidation의 검증 로직을 실행해야 하기 때문에 RetentionPolicy를 RUNTIME으로 지정해주어야합니다.
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {
    String message() default "유효하지 않은 사용자 이름입니다.";

    Class<?>[] groups() default {}; // 커스텀 애너테이션에 필요한 필수요소입니다. 지금은 몰라도됩니다.

    Class<? extends Payload>[] payload() default {}; // 커스텀 애너테이션에 필요한 필수요소입니다. 지금은 몰라도됩니다.
}
