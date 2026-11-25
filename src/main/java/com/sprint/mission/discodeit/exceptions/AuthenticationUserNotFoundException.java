package com.sprint.mission.discodeit.exceptions;

public class AuthenticationUserNotFoundException extends RuntimeException {

    public AuthenticationUserNotFoundException(String message) {
        super(message);
    }
}