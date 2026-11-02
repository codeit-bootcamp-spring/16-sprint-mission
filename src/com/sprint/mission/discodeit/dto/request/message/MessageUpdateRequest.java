package com.sprint.mission.discodeit.dto.request.message;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID id,
        String content
) {
}