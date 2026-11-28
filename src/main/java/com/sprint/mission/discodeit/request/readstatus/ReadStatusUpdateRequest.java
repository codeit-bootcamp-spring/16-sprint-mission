package com.sprint.mission.discodeit.request.readstatus;

import jakarta.validation.constraints.NotNull; // null 값 방지

import java.time.Instant;

public record ReadStatusUpdateRequest(

        @NotNull(message = "마지막 읽은 시각은 필수입니다.")
        Instant newLastReadAt
) {
}