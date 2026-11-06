package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 여러 컨트롤러에서 발생한 예외를 한곳에서 처리합니다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서비스가 IllegalArgumentException을 던지면 400 응답으로 변환합니다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        // 서비스에서 지정한 오류 메시지를 응답 본문에 담습니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    // Spring이 요청 본문을 DTO로 변환하지 못하면 호출됩니다.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody() {
        // JSON 문법이나 필드 값이 잘못됐을 때 공통 안내를 반환합니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("요청 본문의 JSON 형식과 필드 값을 확인해주세요."));
    }

    // 두 메서드가 같은 형식의 JSON 응답을 반환하도록 정의합니다.
    public record ErrorResponse(String message) {
    }
}