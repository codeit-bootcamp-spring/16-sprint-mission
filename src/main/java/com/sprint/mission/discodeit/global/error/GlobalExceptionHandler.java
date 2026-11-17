package com.sprint.mission.discodeit.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 데이터를 찾지 못했을 때 (404 Not Found)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseStatusException handleNoSuchElementException() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "요청하신 데이터를 찾을 수 없습니다.");
    }

    // 비즈니스 로직 검증 오류 (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseStatusException handleIllegalArgumentException() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청 정보입니다. 입력값을 확인해 주세요.");
    }

    // 잘못된 JSON 데이터 요청 처리 (400 Bad Request)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseStatusException handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "요청 본문(JSON)의 형식이 잘못되었거나 파싱할 수 없습니다.");
    }

    // 지원하지 않는 HTTP 메서드로 요청했을 때 (405 Method Not Allowed)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseStatusException handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드 요청입니다. (지원 메서드: " + e.getSupportedHttpMethods() + ")");
    }

    // 지원하지 않는 미디어 타입(Content-Type)으로 요청했을 때 (415 Unsupported Media Type)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseStatusException handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입(Content-Type)입니다. (지원 타입: " + e.getSupportedMediaTypes() + ")");
    }

    // 그 외 예측하지 못한 서버 내부 에러 처리 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseStatusException handleAllException(Exception e) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.");
    }
}

