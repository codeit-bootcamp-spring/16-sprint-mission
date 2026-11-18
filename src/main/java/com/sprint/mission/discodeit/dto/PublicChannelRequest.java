package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PublicChannelRequest(@Schema(description = "공개 채널 이름", example = "general") String name,
                                   @Schema(description = "공개 채널 설명", example = "자유롭게 이야기하는 채널") String description) {
}
