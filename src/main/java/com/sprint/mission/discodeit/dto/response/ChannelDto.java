package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id, Instant createdAt, Instant updatedAt,
        UUID ownerId, ChannelType type, String name, String description,
        List<UUID> participantIds, Instant lastMessageAt
) {}
