package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageUpdateRequest(
        @Schema(
                description = "변경할 메시지 내용",
                example = "수정된 메시지입니다."
        )
        String newContent
) {
}
