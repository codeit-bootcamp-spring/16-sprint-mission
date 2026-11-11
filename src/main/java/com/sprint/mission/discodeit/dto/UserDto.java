package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserDto(UUID id,
                      String username,
                      String email,
                      UUID profileId,
                      Instant createdAt,
                      Instant updatedAt,
                      boolean online) {

    public UserDto toDto(User user, UserStatus userStatus){
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                userStatus.isOnline()
        );
    }
}