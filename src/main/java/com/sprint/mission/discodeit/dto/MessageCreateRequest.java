package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record MessageCreateRequest(@Schema(description = "등록할 메시지 내용", example = "안녕하세요") String comment,
                                   @Schema(description = "메시지를 작성하는 사용자 ID") UUID authorId,
                                   @Schema(description = "메시지를 등록할 채널 ID") UUID channelId) {
}