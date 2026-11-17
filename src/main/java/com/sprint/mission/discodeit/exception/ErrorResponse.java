package com.sprint.mission.discodeit.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String message
) {
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(Instant.now(), status, message);
    }
}