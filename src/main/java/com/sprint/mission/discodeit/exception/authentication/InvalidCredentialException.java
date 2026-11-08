package com.sprint.mission.discodeit.exception.authentication;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(String message) {
        super(message);
    }
}
