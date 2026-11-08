package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;

public record PublicChannelInfo (
        @Override
        Channel channel,
        @Override
        Instant lastMessageAt
) implements ChannelInfo {
}
