package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreateRequest(
        @NotNull
        UUID authorId,

        @Schema(example = "안녕하세요!")
        String content,

        @NotNull
        UUID channelId
) {
}
