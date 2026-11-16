package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PublicChannelCreateRequest(
        @Schema(example = "위클리 페이퍼")
        @NotBlank
        String name,

        @Schema(example = "매주 위클리 페이퍼 토픽을 제공하는 채널입니다.")
        @NotBlank
        String description
) {
}
