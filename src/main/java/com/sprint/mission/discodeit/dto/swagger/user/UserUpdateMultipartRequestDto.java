package com.sprint.mission.discodeit.dto.swagger.user;

import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserUpdateMultipartRequest")
public class UserUpdateMultipartRequestDto {

    @NotNull
    @Schema(description = "수정할 User 정보")
    private UserUpdateRequestDto userUpdateRequest;

    @Schema(
            description = "수정할 User 프로필 이미지",
            type = "string",
            format = "binary"
    )
    private MultipartFile profile;
}