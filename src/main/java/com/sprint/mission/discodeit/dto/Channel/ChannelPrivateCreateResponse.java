package com.sprint.mission.discodeit.dto.Channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record ChannelPrivateCreateResponse(
        UUID id,
        List<UUID> memberIds,
        ChannelType type
) {
    public static ChannelPrivateCreateResponse from(Channel channel) {
        return new ChannelPrivateCreateResponse(
                channel.getId(),
                List.copyOf(channel.getUsers()),
                channel.getType()
        );
    }
}
