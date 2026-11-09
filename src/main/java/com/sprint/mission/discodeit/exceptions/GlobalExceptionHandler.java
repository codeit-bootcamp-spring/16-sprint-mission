package com.sprint.mission.discodeit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 요청 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException iaException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(iaException.getMessage());
    }

    // 데이터를 찾을 수 없을 때 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException rnfException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(rnfException.getMessage());
    }

    // 데이터가 중복될 때 처리
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResourceException(DuplicateResourceException drException) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(drException.getMessage());
    }
}