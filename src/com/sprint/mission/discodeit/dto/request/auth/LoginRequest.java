package com.sprint.mission.discodeit.dto.request.auth;

public record LoginRequest(
        String username,
        String password
) {
}