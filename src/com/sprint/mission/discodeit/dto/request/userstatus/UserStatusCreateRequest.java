package com.sprint.mission.discodeit.dto.request.userstatus;

import java.util.UUID;

public record UserStatusCreateRequest(
        UUID userId
) {
}