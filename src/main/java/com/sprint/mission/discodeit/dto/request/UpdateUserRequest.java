package com.sprint.mission.discodeit.dto.request;

public record UpdateUserRequest(
        String username,
        String email,
        String password,
        BinaryContentRequest profile
) {}
