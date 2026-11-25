package com.sprint.mission.discodeit.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        @Schema(description = "사용자명", example = "student01")
        String username,

        @NotBlank
        @Schema(description = "비밀번호", example = "Example123!")
        String password
) {
}
