package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;

public record PublicChannelUpdateRequest(
        @Schema(example = "위클리 페이퍼")
        String newName,

        @Schema(example = "매주 위클리 페이퍼 토픽을 제공하는 채널입니다.")
        String newDescription
) {
}
