package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @ArraySchema(
                arraySchema = @Schema(description = "비공개 채널에 참여할 사용자들의 ID"),
                schema = @Schema(
                        description = "참여자 ID",
                        example = "550e8400-e29b-41d4-a716-446655440000"
                )
        )
        @NotNull
        List<@NotNull UUID> participantIds
) {
}
