package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다. %s"),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다. %s"),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다. %s"),

    PRIVATE_CHANNEL_CANNOT_BE_UPDATED(
            HttpStatus.BAD_REQUEST,
            "비공개 채널은 수정할 수 없습니다."
    );

    private final HttpStatus status;
    private final String message;
}
