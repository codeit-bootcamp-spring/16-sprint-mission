package com.sprint.mission.discodeit.dto.request.status;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(

        @NotNull(message = "마지막 읽은 시각은 필수입니다.")
        Instant lastReadAt
) { }