package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.util.UUID;

public record UserUpdateDto(
        UUID id,
        String username,
        Integer age,
        String password,
        BinaryContentDto profile
) {
}
