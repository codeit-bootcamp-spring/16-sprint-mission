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
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "읽음 상태를 찾을 수 없습니다."),

    PRIVATE_CHANNEL_CANNOT_BE_UPDATED(
            HttpStatus.BAD_REQUEST,
            "비공개 채널은 수정할 수 없습니다."
    ),

    READ_STATUS_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            "이미 해당 사용자의 채널 읽음 상태가 존재합니다."
    ),

    USER_NOT_PARTICIPANT(
            HttpStatus.BAD_REQUEST,
            "사용자가 해당 채널에 참여하고 있지 않습니다."
    ),

    USERNAME_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            "이미 사용 중인 사용자 이름입니다."
    ),

    EMAIL_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            "이미 사용 중인 이메일입니다."
    );

    private final HttpStatus status;
    private final String message;
}
