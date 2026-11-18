package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ChannelUpdateRequest(@Schema(hidden = true) UUID channelId,
                                   @Schema(description = "변경할 공개 채널 이름", example = "announcements") String newName,
                                   @Schema(description = "변경할 공개 채널 설명", example = "공지사항을 공유하는 채널") String newDescription) {
}