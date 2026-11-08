package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.validation.Password;
import com.sprint.mission.discodeit.validation.Username;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record  UserUpdateRequest(
        // UUID userId, // userId는 포함하지 않숩나더, update()의 인자로 따로 갑니다.
        @NotBlank // null, 빈 문자열, 공백만 있는 문자열을 거부합니다.
        @Username
        String newUsername,

        @NotBlank
        @Email
        @Size(max = 254)
        String newEmail,

        @NotBlank
        @Password
        String newPassword,
        // UUID newProfileId,
        @Nullable BinaryContentCreateRequest profile
) {
}
