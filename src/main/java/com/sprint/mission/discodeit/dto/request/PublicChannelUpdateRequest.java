package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공개 채널 정보 수정 요청")
public record PublicChannelUpdateRequest(
        @Schema(description = "새 채널 이름", example = "announcements")
        String newName,

        @Schema(description = "새 채널 설명", example = "공지사항을 공유하는 채널")
        String newDescription
) {
}