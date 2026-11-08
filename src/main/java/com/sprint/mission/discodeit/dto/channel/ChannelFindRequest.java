package com.sprint.mission.discodeit.dto.channel;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record ChannelFindRequest(
        UUID channelId,
        @Nullable UUID userId
) {
}
