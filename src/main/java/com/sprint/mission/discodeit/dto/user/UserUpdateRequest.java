package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Schema(example = "김코드")
        String newUsername,

        @Schema(example = "kimcode123@codeit.com")
        @Email
        String newEmail,

        @Schema(example = "asdfqwer!")
        @Size(min = 8, max = 20)
        String newPassword
) {
}
