package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @Schema(example = "김코드")
        @NotBlank
        String username,

        @Schema(example = "kimcode123@codeit.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "비밀번호 (8자 이상 ~ 20자 이하)", example = "asdfqwer!")
        @NotBlank
        @Size(min = 8, max = 20)
        String password
) {
}
