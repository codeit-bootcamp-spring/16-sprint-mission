package com.sprint.mission.discodeit.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채널 수정 요청")
public record UpdateChannelRequest(
        @Schema(description = "새 채널명", example = "자유게시판")
        String newName,
        @Schema(description = "새 채널 설명", example = "자유롭게 이야기하는 채널입니다.")
        String newDescription
) {
}
