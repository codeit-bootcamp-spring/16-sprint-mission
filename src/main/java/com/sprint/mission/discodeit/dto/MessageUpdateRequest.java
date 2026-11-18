package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record MessageUpdateRequest(@Schema(hidden = true) UUID messageId,
                                   @Schema(description = "수정할 메시지 내용", example = "내일 오전에 다시 확인하겠습니다") String newContent) {
}