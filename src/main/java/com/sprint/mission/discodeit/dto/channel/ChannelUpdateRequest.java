package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChannelUpdateRequest(
        @Schema(description = "변경할 공개 채널 이름", example = "announcements")
        String newName,

        @Schema(description = "변경할 공개 채널 설명", example = "공지사항을 공유하는 채널입니다.")
        String newDescription
) {
}
