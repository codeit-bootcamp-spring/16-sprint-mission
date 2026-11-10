package com.sprint.mission.discodeit.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //데이터를 찾지 못했을 때 (404 Not Found)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseStatusException handleNoSuchElementException() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "요청하신 데이터를 찾을 수 없습니다.");
    }

    //잘못된 인자 요청 및 비즈니스 로직 오류 (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseStatusException handleIllegalArgumentException() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청 정보입니다. 입력값을 확인해 주세요.");
    }
    // 그 외 내부 에러 처리 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseStatusException handleAllException() {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }
}

