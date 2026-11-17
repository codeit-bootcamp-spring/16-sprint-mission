package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserStatusCreateRequest(
        @NotNull
        UUID userId
) {
}
