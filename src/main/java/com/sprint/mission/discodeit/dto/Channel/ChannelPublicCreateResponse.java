package com.sprint.mission.discodeit.dto.Channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelPublicCreateResponse(
        UUID id,
        String name,
        String description,
        ChannelType type
) {
    public static ChannelPublicCreateResponse from(Channel channel) {
        return new ChannelPublicCreateResponse(
                channel.getId(), channel.getName(), channel.getDescription(), channel.getType());
    }
}
