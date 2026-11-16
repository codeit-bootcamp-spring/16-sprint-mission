package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 등록 요청")
public record MessageCreateRequest(
        @Schema(description = "메시지 본문", example = "안녕하세요!")
        String content,

        @Schema(description = "메시지를 보낼 채널의 UUID")
        UUID channelId,

        @Schema(description = "메시지를 작성하는 사용자의 UUID")
        UUID authorId
) {
}
