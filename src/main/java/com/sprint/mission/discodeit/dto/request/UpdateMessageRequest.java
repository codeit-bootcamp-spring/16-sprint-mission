package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 수정 요청")
public record UpdateMessageRequest(
        @Schema(description = "새 메시지 내용", example = "수정된 메시지입니다")
        String newContent
) {
}
