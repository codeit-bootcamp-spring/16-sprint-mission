package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;

public interface ChannelInfo {
    Channel channel();
    Instant lastMessageAt();
}
