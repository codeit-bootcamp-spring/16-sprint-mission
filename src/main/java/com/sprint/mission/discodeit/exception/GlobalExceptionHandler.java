package com.sprint.mission.discodeit.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

// 여러 컨트롤러에 공통적으로 적용할 기능을 정의하는 스프링 애너테이션입니다.
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        // IllegalArgumentException 객체에 저장된 예외 메시지 문자열을 반환합니다.
        // 예를 들면, throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다: " + username);
        // 에서 ()안에 있는 문자열을 출력합니다.
        return exception.getMessage();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception) {
        return exception.getMessage();
    }
}
