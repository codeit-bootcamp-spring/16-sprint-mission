package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.authentication.InvalidCredentialException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelCreateRequestException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelUpdateRequestException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelModificationNotAllowedException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {

        return exception.getMessage();
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateResourceException(DuplicateResourceException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(UserNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(UserStatusNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserStatusNotFoundException(UserStatusNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(InvalidCredentialException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleInvalidCredentialException(InvalidCredentialException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(InvalidChannelCreateRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidChannelCreateRequestException(InvalidChannelCreateRequestException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(PrivateChannelModificationNotAllowedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handlePrivateChannelModificationNotAllowedException(PrivateChannelModificationNotAllowedException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(ChannelNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleChannelNotFoundException(ChannelNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(InvalidChannelUpdateRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidChannelUpdateRequestException(InvalidChannelUpdateRequestException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleMessageNotFoundException(MessageNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(ReadStatusNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleReadStatusNotFoundException(ReadStatusNotFoundException exception) {
        return exception.getMessage();
    }
}
