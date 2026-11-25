package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 본문 수정 요청")
public record MessageUpdateRequest(
        @Schema(description = "수정할 메시지의 새 본문", example = "수정한 메시지입니다.")
        String newContent
) {
}