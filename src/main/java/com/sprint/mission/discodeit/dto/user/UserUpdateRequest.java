package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.validation.Password;
import com.sprint.mission.discodeit.validation.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record  UserUpdateRequest(
        @Username
        String newUsername,

        @Email
        @Size(max = 254)
        String newEmail,

        @Password
        String newPassword
) {
}
