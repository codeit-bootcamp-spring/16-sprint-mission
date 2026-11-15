package com.sprint.mission.discodeit.dto.request.status;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UserStatusUpdateRequest")
public record UserStatusUpdateRequest(

        @NotNull(message = "마지막 접속 시간은 필수입니다.")
        Instant newLastActiveAt
) {
}