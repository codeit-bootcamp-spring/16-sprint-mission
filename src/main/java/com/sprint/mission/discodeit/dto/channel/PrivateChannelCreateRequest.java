package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @NotEmpty
        @Schema(
                description = "비공개 채널에 참여할 사용자 UUID 목록",
                example = "[\"550e8400-e29b-41d4-a716-446655440001\", \"550e8400-e29b-41d4-a716-446655440002\"]"
        )
        List<UUID> participantIds
) {
}
