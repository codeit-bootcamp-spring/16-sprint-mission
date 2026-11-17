package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

public record PublicChannelCreateRequest(
        @NotBlank
        @Schema(description = "공개 채널 이름", example = "general")
        String name,

        @Nullable
        @Schema(description = "공개 채널 설명", example = "자유롭게 대화하는 채널입니다.")
        String description
) {
}
