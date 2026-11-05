package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ChannelLeaveRequest(
        @NotBlank
        UUID userId
) {
}
