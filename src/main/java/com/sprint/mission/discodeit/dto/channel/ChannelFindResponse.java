package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelFindResponse(
        UUID id,
        ChannelType type,

        @Schema(example = "위클리 페이퍼")
        String name,

        @Schema(example = "매주 위클리 페이퍼 토픽을 제공하는 채널입니다.")
        String description,

        List<UUID> participantIds,
        
        Instant lastMessageAt
) {
    public static ChannelFindResponse of(Channel channel, Instant lastMessageAt) {
        return new ChannelFindResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                List.copyOf(channel.getUsers()),
                lastMessageAt
        );
    }
}
