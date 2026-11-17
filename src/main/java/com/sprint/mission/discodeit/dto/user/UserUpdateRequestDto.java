package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    @Schema(example = "user1234")
    private String newUsername;

    @Schema(example = "user1234@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String newEmail;

    @Schema(example = "1234")
    private String newPassword;
}
