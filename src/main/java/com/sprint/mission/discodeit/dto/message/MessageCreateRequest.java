package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreateRequest(
        @NotBlank
        @Schema(description = "메시지 내용", example = "안녕하세요!")
        String content,

        @NotNull
        @Schema(
                description = "메시지를 작성할 채널 UUID",
                example = "550e8400-e29b-41d4-a716-446655440001"
        )
        UUID channelId,

        @NotNull
        @Schema(
                description = "메시지 작성자 UUID",
                example = "550e8400-e29b-41d4-a716-446655440002"
        )
        UUID authorId
) {
}
