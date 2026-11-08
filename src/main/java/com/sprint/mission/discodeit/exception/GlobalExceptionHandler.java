package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.authentication.InvalidCredentialException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelCreateRequestException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelUpdateRequestException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelModificationNotAllowedException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 여러 컨트롤러에 공통적으로 적용할 기능을 정의하는 스프링 애너테이션입니다.
// @ControllerAdvice에서는 해당 문자열이 응답 본문이 아니라 뷰 이름으로 해석될 수 있습니다.
// @ControllerAdvice
// 따라서 @RestControllerAdvice 를 사용해야합니다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        // IllegalArgumentException 객체에 저장된 예외 메시지 문자열을 반환합니다.
        // 예를 들면, throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다: " + username);
        // 에서 ()안에 있는 문자열을 출력합니다.
        return exception.getMessage();
    }

    @ExceptionHandler(DuplicateResourceException.class)
    // HTTP 상태 코드를 409 CONFLICT로 설정합니다.
    // ResponseEntity로 상태 코드를 직접 지정할 수도 있습니다.
    // 실무에서는 GlobalExceptionHandler 내부에서 ResponseEntity를 반환하는 방식도 존재합니다.
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

    /*
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception) {
        return exception.getMessage();
    }
     */
}
