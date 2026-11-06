package com.sprint.mission.discodeit.dto.authentication;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String userName
) {
}
