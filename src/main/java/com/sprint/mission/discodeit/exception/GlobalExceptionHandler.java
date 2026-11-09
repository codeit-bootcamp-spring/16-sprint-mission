package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return toResponse(HttpStatus.BAD_REQUEST, "INVALID_INPUT", e);
    }

    @ExceptionHandler(AlreadyJoinedChannelException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyJoinedChannel(AlreadyJoinedChannelException e) {
        return toResponse(HttpStatus.CONFLICT, "ALREADY_JOINED_CHANNEL", e);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        return toResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        return toResponse(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", e);
    }

    @ExceptionHandler(UserStatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserStatusNotFound(UserStatusNotFoundException e) {
        return toResponse(HttpStatus.NOT_FOUND, "USER_STATUS_NOT_FOUND", e);
    }

    @ExceptionHandler(BinaryContentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(BinaryContentNotFoundException e) {
        return toResponse(HttpStatus.NOT_FOUND, "FILE_NOT_FOUND", e);
    }

    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChannelNotFound(ChannelNotFoundException e) {
        return toResponse(HttpStatus.NOT_FOUND, "CHANNEL_NOT_FOUND", e);
    }

    @ExceptionHandler(PrivateChannelUpdateNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handlePrivateChannelUpdateNotAllowed(PrivateChannelUpdateNotAllowedException e) {
        return toResponse(HttpStatus.BAD_REQUEST, "PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED", e);
    }

    @ExceptionHandler(PrivateChannelJoinNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handlePrivateChannelJoinNotAllowed(PrivateChannelJoinNotAllowedException e) {
        return toResponse(HttpStatus.BAD_REQUEST, "PRIVATE_CHANNEL_JOIN_NOT_ALLOWED", e);
    }

    @ExceptionHandler(ChannelOwnerLeaveNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleChannelOwnerLeaveNotAllowed(ChannelOwnerLeaveNotAllowedException e) {
        return toResponse(HttpStatus.BAD_REQUEST, "CHANNEL_OWNER_LEAVE_NOT_ALLOWED", e);
    }

    @ExceptionHandler(ChannelOwnershipTransferNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleChannelOwnershipTransferNotAllowed(ChannelOwnershipTransferNotAllowedException e) {
        return toResponse(HttpStatus.BAD_REQUEST, "CHANNEL_OWNERSHIP_TRANSFER_NOT_ALLOWED", e);
    }

    @ExceptionHandler(ChannelOwnerRequiredException.class)
    public ResponseEntity<ErrorResponse> handleChannelOwnerRequired(ChannelOwnerRequiredException e) {
        return toResponse(HttpStatus.FORBIDDEN, "CHANNEL_OWNER_REQUIRED", e);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotFound(MessageNotFoundException e) {
        return toResponse(HttpStatus.NOT_FOUND, "MESSAGE_NOT_FOUND", e);
    }

    @ExceptionHandler(ChannelMemberRequiredException.class)
    public ResponseEntity<ErrorResponse> handleChannelMemberRequired(ChannelMemberRequiredException e) {
        return toResponse(HttpStatus.FORBIDDEN, "CHANNEL_MEMBER_REQUIRED", e);
    }

    @ExceptionHandler(MessageAuthorRequiredException.class)
    public ResponseEntity<ErrorResponse> handleMessageAuthorRequired(MessageAuthorRequiredException e) {
        return toResponse(HttpStatus.FORBIDDEN, "MESSAGE_AUTHOR_REQUIRED", e);
    }

    @ExceptionHandler(DuplicateReadStatusException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReadStatus(DuplicateReadStatusException e) {
        return toResponse(HttpStatus.CONFLICT, "DUPLICATE_READ_STATUS", e);
    }

    @ExceptionHandler(ReadStatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReadStatusNotFound(ReadStatusNotFoundException e) {
        return toResponse(HttpStatus.NOT_FOUND, "READ_STATUS_NOT_FOUND", e);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
        return toResponse(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", e);
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUsername(DuplicateUsernameException e) {
        return toResponse(HttpStatus.CONFLICT, "DUPLICATE_USERNAME", e);
    }

    @ExceptionHandler(DuplicateUserStatusException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUserStatus(DuplicateUserStatusException e) {
        return toResponse(HttpStatus.CONFLICT, "DUPLICATE_USER_STATUS", e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }

    private ResponseEntity<ErrorResponse> toResponse(HttpStatus status, String code, Exception e) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, e.getMessage()));
    }
}
