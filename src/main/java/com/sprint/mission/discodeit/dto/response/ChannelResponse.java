package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채널 정보")
public record ChannelResponse(
        @Schema(description = "채널 id", example = "22222222-2222-4222-8222-222222222222")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-11-13T00:00:00Z")
        String createdAt,
        @Schema(description = "마지막 수정 시각", example = "2026-11-13T01:00:00Z")
        String updatedAt,
        @Schema(description = "채널 종류", example = "PUBLIC")
        ChannelType type,
        @Schema(description = "채널명", example = "공지사항")
        String name,
        @Schema(description = "채널 설명", example = "공지사항을 올리는 채널입니다.")
        String description
) {
    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(channel.getId(), channel.getCreatedAt().toString(),
                channel.getUpdatedAt().toString(), channel.getType(), channel.getName(), channel.getDescription());
    }
}
