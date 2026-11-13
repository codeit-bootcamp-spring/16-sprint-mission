package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public BusinessLogicException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public BusinessLogicException(
            ExceptionCode exceptionCode,
            Object... args) {
        super(String.format(exceptionCode.getMessage(), args));
        this.exceptionCode = exceptionCode;
    }

}
