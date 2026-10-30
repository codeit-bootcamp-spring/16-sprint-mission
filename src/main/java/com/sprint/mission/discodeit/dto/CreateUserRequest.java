package com.sprint.mission.discodeit.dto;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        CreateBinaryContentRequest profile
) {
}