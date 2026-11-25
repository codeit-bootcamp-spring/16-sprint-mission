package com.sprint.mission.discodeit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Schema(description = "오류 응답")
public record ErrorResponse(
        @Schema(description = "오류 발생 시각", example = "2026-11-13T00:00:00Z")
        Instant timestamp,
        @Schema(description = "HTTP 상태 코드", example = "400")
        int status,
        @Schema(description = "HTTP 상태 이름", example = "Bad Request")
        String error,
        @Schema(description = "오류 원인", example = "유저명 또는 패스워드가 잘못됨")
        String message,
        @Schema(description = "요청 경로", example = "/api/auth/login")
        String path
) {
    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message, path);
    }
}
