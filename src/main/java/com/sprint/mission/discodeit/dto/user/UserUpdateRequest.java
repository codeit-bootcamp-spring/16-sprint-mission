package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.validation.Password;
import com.sprint.mission.discodeit.validation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record  UserUpdateRequest(
        @Username
        @Schema(description = "변경할 사용자명", example = "student02")
                String newUsername,

        @Email
        @Size(max = 254)
        @Schema(description = "변경할 이메일", example = "student02@example.com")
        String newEmail,

        @Password
        @Schema(description = "변경할 비밀번호", example = "NewPassword123!")
        String newPassword
) {
}
