package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FindChannelResponse(
        UUID id,
        ChannelType channelType,
        String name,
        String description,
        Instant lastMessageAt,
        List<UUID> participantIds
) {

    public static FindChannelResponse from(Channel channel, Instant lastMessageAt, List<UUID> participantIds) {
        return new FindChannelResponse(channel.getId(), channel.getType(), channel.getName(),
                channel.getDescription(), lastMessageAt, participantIds);
    }
}
