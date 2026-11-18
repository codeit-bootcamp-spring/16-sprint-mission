package com.sprint.mission.discodeit.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "에러 응답 DTO")
@Getter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "ERROR_CODE")
    private String code;

    @Schema(description = "에러 메세지", example = "Error message")
    private String message;
}
