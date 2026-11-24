package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageUpdateRequest(
        @Schema(example = "안녕하세요!")
        String newContent
) {
}
