package com.sprint.mission.discodeit.dto.request.readstatus;

import java.util.UUID;

import jakarta.validation.constraints.NotNull; // null 값 방지

public record ReadStatusCreateRequest(

        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId) {
}