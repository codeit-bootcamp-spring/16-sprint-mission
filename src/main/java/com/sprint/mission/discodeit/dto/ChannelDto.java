package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.type.ChannelType;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ChannelDto(UUID id,
                         ChannelType type,
                         String name,
                         String description,
                         Set<UUID> participantIds,
                         Instant createdAt,
                         Instant updatedAt,
                         Instant lastMessageAt
) {
}
