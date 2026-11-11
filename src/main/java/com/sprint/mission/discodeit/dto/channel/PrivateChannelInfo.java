package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PrivateChannelInfo(
        @Override
        Channel channel,
        @Override
        Instant lastMessageAt,
        Set<UUID> userIdList
) implements ChannelInfo {
}
