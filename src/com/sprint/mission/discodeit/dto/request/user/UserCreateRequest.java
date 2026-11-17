package com.sprint.mission.discodeit.dto.request.user;

public record UserCreateRequest(
        String username,
        String email,
        String password
) {
}