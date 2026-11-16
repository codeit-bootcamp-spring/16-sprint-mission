package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공개 채널 생성 요청")
public record PublicChannelCreateRequest(
        @Schema(description = "채널 이름", example = "general")
        String name,

        @Schema(description = "채널 설명", example = "자유롭게 대화하는 채널")
        String description
) {
}