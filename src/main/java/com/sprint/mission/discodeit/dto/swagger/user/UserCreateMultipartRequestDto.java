package com.sprint.mission.discodeit.dto.swagger.user;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserCreateMultipartRequest")
public class UserCreateMultipartRequestDto {

    @NotNull
    @Schema(description = "User 생성 정보")
    private UserCreateRequestDto userCreateRequest;

    @Schema(
            description = "User 프로필 이미지",
            type = "string",
            format = "binary"
    )
    private MultipartFile profile;
}
