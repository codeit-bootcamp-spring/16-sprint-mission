package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,

        @Schema(example = "김코드")
        String username,

        @Schema(example = "kimcode123@codeit.com")
        String email,

        @Schema(description = "비밀번호 (8자 이상 ~ 20자 이하)", example = "asdfqwer!")
        String password,
        
        UUID profileId
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getProfileId()
        );
    }
}
