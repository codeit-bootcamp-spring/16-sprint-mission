package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

public record UserCreateDto(
    String username,
    int age,
    String email,
    String password,
    BinaryContentDto profile
) {
}
