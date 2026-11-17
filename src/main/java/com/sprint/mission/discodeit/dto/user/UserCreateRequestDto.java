
package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequestDto {

    @Schema(example = "user1234")
    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @Schema(example = "user1234@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(example = "1234")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}