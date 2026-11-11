package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType channelType,
        String name,
        String description
) {
    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(channel.getId(), channel.getType(),
                channel.getName(), channel.getDescription());
    }
}
