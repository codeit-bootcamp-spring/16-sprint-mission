package com.sprint.mission.discodeit.service.validator;

import org.springframework.stereotype.Component;

@Component
public class CommonValidator {

    public void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public void validatePositiveOrZero(int value, String message) {
        if (value < 0) {
            throw new IllegalArgumentException(message);
        }
    }
}