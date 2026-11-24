package com.sprint.mission.discodeit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthUserRequest(
        @Schema(example = "김코드")
        @NotBlank
        String username,

        @Schema(description = "비밀번호 (8자 이상 ~ 20자 이하", example = "asdfqwer!")
        @NotBlank @Size(min = 8, max = 20)
        String password
) {
}
