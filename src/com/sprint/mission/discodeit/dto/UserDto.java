package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        UUID profileImageId,
        boolean online
) {
}