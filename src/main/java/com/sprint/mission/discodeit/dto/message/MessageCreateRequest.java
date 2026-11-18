package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreateRequest(
        @NotNull
        UUID authorId,

        String content,

        @NotNull
        UUID channelId
) {
}
