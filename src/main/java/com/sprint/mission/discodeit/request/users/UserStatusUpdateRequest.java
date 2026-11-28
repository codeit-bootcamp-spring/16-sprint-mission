package com.sprint.mission.discodeit.request.users;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "UserStatusUpdateRequest",
        description = "변경할 User 온라인 상태 정보"
)
public record UserStatusUpdateRequest(

        @NotNull(message = "마지막 접속 시간은 필수입니다.")
        Instant newLastActiveAt
) {
}