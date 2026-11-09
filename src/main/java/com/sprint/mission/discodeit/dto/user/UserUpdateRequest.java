package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.validation.Password;
import com.sprint.mission.discodeit.validation.Username;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record  UserUpdateRequest(
        @NotBlank
        @Username
        String newUsername,

        @NotBlank
        @Email
        @Size(max = 254)
        String newEmail,

        @NotBlank
        @Password
        String newPassword,

        @Nullable BinaryContentCreateRequest profile
) {
}
