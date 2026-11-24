package com.sprint.mission.discodeit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginRequestDto {

    @Schema(example = "user1234")
    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @Schema(example = "1234")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
