package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공개 채널 생성 요청")
public record CreatePublicChannelRequest(
        @Schema(description = "채널명", example = "공지사항")
        String name,
        @Schema(description = "채널 설명", example = "공지사항을 올리는 채널입니다.")
        String description
) {}
