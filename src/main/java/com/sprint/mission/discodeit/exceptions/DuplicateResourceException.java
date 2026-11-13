package com.sprint.mission.discodeit.exceptions;

// 데이터가 중복일 때 예외가 발생하는 클래스
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
