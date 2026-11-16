package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelJoinRequest(
        @Schema(description = "가입하는 사용자 ID")
        @NotNull
        UUID userId
) {

}
