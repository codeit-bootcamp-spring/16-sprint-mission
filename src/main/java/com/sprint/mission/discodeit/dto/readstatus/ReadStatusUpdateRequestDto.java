package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;

import lombok.Builder;

@Builder
public record ReadStatusUpdateRequestDto(
        Instant newLastReadAt
) {
}
