package com.sprint.mission.discodeit.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthUserRequest(
        @NotBlank
        String username,

        @NotBlank @Size(min = 8, max = 20)
        String password
) {
}
