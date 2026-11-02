package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageUpdateDto(
        UUID id,
        String content
) {
}
