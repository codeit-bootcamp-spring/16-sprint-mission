package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record AuthUserDto(
        UUID id,
        String username,
        String email,
        UUID profileId
) {}
