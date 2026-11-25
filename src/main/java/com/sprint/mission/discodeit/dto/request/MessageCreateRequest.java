package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 생성 요청")
public record MessageCreateRequest(
        @Schema(description = "메시지 내용", example = "안녕하세요")
        String content,
        @Schema(description = "메시지를 보낼 채널 id", example = "22222222-2222-4222-8222-222222222222")
        UUID channelId,
        @Schema(description = "작성자(사용자) id", example = "11111111-1111-4111-8111-111111111111")
        UUID authorId
) {
}
