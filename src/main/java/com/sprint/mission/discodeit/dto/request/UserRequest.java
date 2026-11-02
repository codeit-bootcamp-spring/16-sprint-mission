package com.sprint.mission.discodeit.dto.request;

public record UserRequest(
        String username,
        String email,
        String password,
        BinaryContentRequest profile
) {}
