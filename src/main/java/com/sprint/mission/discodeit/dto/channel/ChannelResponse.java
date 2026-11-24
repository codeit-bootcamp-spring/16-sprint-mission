package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        ChannelType type,

        @Schema(example = "위클리 페이퍼")
        String name,

        @Schema(example = "매주 위클리 페이퍼 토픽을 제공하는 채널입니다.")
        String description
) {
    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
    }
}
