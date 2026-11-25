package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Pattern(regexp = ".*\\S.*", message = "공백만 입력할 수 없습니다.") @Size(max = 32) String newUsername,
    @Email @Size(max = 254) String newEmail,
    @Pattern(regexp = ".*\\S.*", message = "공백만 입력할 수 없습니다.") @Size(max = 72) String newPassword) {}
