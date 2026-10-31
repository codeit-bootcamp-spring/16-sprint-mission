package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class ChannelDto {
    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final ChannelType type;
    private final String name;
    private final String description;
    private final Instant latestMessageAt;
    private final List<UUID> participantIds;

    public ChannelDto(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description,
            Instant latestMessageAt,
            List<UUID> participantIds
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.name = name;
        this.description = description;
        this.latestMessageAt = latestMessageAt;
        this.participantIds = participantIds;
    }
}