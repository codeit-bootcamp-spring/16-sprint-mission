package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateDto(
        String username,
        String email,
        String password,
        UUID profileImageId
) { }
