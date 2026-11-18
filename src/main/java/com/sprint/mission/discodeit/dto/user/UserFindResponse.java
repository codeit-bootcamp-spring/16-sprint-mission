package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record UserFindResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,

        @Schema(example = "김코드")
        String username,

        @Schema(example = "kimcode123@codeit.com")
        String email,

        UUID profileId,
        boolean online
) {
    public static UserFindResponse of(User user, boolean isOnline) {
        return new UserFindResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                user.getEmail(),
                user.getProfileId(),
                isOnline
        );
    }
}
