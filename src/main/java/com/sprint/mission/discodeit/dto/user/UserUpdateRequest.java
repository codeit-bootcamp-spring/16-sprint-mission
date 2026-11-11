package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        String newUsername,

        @Email
        String newEmail,

        @Size(min = 8, max = 20)
        String newPassword
) {
}
