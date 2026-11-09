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

    // 데이터를 찾을 수 없을 때 처리(상태코드 : 404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException rnfException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(rnfException.getMessage());
    }

    // 데이터가 중복될 때 처리(상태코드 : 409)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResourceException(DuplicateResourceException drException) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(drException.getMessage());
    }

    // 접근 권한이 없어 요청이 거부됨(상태코드 : 403)
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<String> handleForbiddenOperationException(ForbiddenOperationException foException) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(foException.getMessage());
    }

    // 유효한 인증 정보가 없어 요청이 거부된 상태(상태코드 : 401)
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<String> handleAuthenticationFailedException(AuthenticationFailedException afException) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(afException.getMessage());
    }
}