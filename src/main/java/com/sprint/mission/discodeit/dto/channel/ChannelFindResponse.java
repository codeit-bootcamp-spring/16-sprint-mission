package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelFindResponse(
        UUID id,
        ChannelType type,
        String name,
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
