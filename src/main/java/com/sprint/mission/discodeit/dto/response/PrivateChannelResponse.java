package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비공개 채널 정보")
public record PrivateChannelResponse(
        @Schema(description = "채널 id", example = "22222222-2222-4222-8222-222222222222")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-11-13T00:00:00Z")
        String createdAt,
        @Schema(description = "마지막 수정 시각", example = "2026-11-13T01:00:00Z")
        String updatedAt,
        @Schema(description = "채널 종류", example = "PRIVATE")
        ChannelType type
) {
    public static PrivateChannelResponse from(Channel channel) {
        return new PrivateChannelResponse(channel.getId(), channel.getCreatedAt().toString(),
                channel.getUpdatedAt().toString(), channel.getType());
    }
}
