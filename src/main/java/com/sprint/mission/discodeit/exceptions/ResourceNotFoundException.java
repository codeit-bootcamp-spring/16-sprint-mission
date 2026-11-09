package com.sprint.mission.discodeit.exceptions;

// 데이터를 찾을 수 없을 때 예외가 발생하는 클래스
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
