package com.sprint.mission.discodeit.dto.request;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        BinaryContentRequest profile
) {}
