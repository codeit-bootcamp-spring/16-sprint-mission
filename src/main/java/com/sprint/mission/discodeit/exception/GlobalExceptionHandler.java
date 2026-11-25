package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomErrorResponse> handleConflict(ConflictException e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<CustomErrorResponse> handleLoginFailed(LoginFailedException e) {
        return error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<CustomErrorResponse> handleInvalidRequest(InvalidRequestException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomErrorResponse> handleUnreadableJson(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "JSON 형식이 올바르지 않습니다");
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<CustomErrorResponse> handleMissingPart(MissingServletRequestPartException e) {
        return error(HttpStatus.BAD_REQUEST,
                "필수 요청 파트가 누락되었습니다 " + e.getRequestPartName());
    }


    private ResponseEntity<CustomErrorResponse> error(HttpStatus status, String message) {
        CustomErrorResponse response = new CustomErrorResponse(
                Integer.toString(status.value()), message);
        return ResponseEntity.status(status).body(response);
    }
}