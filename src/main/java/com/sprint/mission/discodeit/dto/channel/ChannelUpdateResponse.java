package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;

public record ChannelUpdateResponse(
        UUID id,
        String name,
        String description
) {
    public static ChannelUpdateResponse from(Channel channel) {
        return new ChannelUpdateResponse(channel.getId(), channel.getName(), channel.getDescription());
    }
}
