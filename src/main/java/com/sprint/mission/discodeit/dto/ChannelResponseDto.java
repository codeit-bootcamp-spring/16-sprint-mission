package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        Instant lastMessageAt,
        List<UUID> userIds
) {
    public static ChannelResponseDto from(Channel channel, Instant lastMessageAt, List<UUID> userIds) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                lastMessageAt,
                userIds
        );
    }
}