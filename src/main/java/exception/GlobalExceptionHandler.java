package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 공통 오류 응답 생성
    private ResponseEntity<Map<String, Object>> errorResponse(
            HttpStatus status,
            String message
    ) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }

    // 1. 존재하지 않는 데이터 조회
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            NoSuchElementException e
    ) {
        return errorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }

    // 2. 잘못된 요청 또는 중복 데이터
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException e
    ) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
    }

    // 3. 요청 데이터 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException e
    ) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                "요청 데이터가 올바르지 않습니다."
        );
    }

    // 4. 잘못된 UUID 등 매개변수 형식 오류
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e
    ) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                "요청 매개변수의 형식이 올바르지 않습니다."
        );
    }

    // 5. 잘못된 JSON 요청
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            HttpMessageNotReadableException e
    ) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                "요청 본문을 읽을 수 없습니다."
        );
    }

    // 6. 첨부파일 용량 초과
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleFileSize(
            MaxUploadSizeExceededException e
    ) {
        return errorResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "업로드 가능한 파일 크기를 초과했습니다."
        );
    }

    // 7. 예상하지 못한 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception e
    ) {

        // 실제 오류 원인을 IntelliJ 실행 창에 출력
        e.printStackTrace();

        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다."
        );
    }
}
