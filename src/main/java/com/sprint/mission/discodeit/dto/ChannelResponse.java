package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant lastMessageAt,
        Set<UUID> participantIds
) {
}