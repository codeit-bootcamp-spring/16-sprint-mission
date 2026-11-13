package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelLeaveRequest(
        @NotNull
        UUID userId
) {
}
