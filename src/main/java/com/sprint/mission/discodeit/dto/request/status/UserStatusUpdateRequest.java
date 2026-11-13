package com.sprint.mission.discodeit.dto.request.status;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(

        @NotNull(message = "마지막 접속 시간은 필수입니다.")
        Instant newLastActiveAt
) {
}