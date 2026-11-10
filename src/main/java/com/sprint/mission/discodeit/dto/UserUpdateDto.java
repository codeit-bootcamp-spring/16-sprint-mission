package com.sprint.mission.discodeit.dto;

public record UserUpdateDto(
        String email,
        String password,
        BinaryContentRequestDto profile
) {}
