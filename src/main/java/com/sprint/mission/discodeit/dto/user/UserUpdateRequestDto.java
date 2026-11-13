package com.sprint.mission.discodeit.dto.user;

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

    private String newUsername;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String newEmail;

    private String newPassword;
}
