package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.validation.Password;
import com.sprint.mission.discodeit.validation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank
        @Username
        @Schema(description = "사용자명", example = "student01")
                String username,

        @NotBlank
        @Email
        @Size(max = 254)
        @Schema(description = "이메일", example = "student01@example.com")
        String email,

        @NotBlank
        @Password
        @Schema(description = "비밀번호", example = "Example123!")
        String password
) {
}
