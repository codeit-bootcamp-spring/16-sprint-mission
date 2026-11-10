package com.sprint.mission.discodeit.dto.user;

public record UserDto(
        String username,
        int age,
        String email,
        boolean online
) {
}
