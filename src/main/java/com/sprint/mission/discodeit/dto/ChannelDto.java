package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.type.ChannelType;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ChannelDto(UUID id,
                         ChannelType type,
                         String channelName,
                         String description,
                         Set<UUID> memberIds,
                         Instant createdAt,
                         Instant updatedAt,
                         Instant lastMessageAt
) {}