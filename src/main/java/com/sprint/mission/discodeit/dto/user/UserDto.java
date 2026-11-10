package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        int age,
        String email,
        UUID profileId,
        boolean online
) {
}