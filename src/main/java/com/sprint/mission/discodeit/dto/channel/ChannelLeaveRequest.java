package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelLeaveRequest(
        @Schema(description = "탈퇴할 사용자 ID")
        @NotNull
        UUID userId
) {
}
