package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        UUID profileId,
        String username,
        String email,
        boolean isOnline,
        Instant createdAt,
        Instant updatedAt
) {    public static UserResponseDto excludePassword(User user, UserStatus userStatus) {
    return new UserResponseDto(
            user.getId(),
            user.getProfileId(),
            user.getUsername(),
            user.getEmail(),
            userStatus != null && userStatus.isOnline(), // 온라인 상태 여부
            user.getCreatedAt(),
            user.getUpdatedAt()
    );
  }
}
