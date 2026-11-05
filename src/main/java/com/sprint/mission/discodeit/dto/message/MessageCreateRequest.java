package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MessageCreateRequest(
        @NotBlank
        UUID authorId,

        String content,

        @NotBlank
        UUID channelId
) {
}
