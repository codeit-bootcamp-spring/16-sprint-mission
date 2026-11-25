package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Component
public class ChannelMapper {
    public ChannelDto toChannelDto(Channel channel) {
        return new ChannelDto(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
    }

    public ChannelResponse toChannelResponse(Channel channel,
                                             Set<UUID> participantIds,
                                             Instant lastMessageAt) {
        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }

    /*
    public PrivateChannelResponseByUserId toPrivateChannelResponseByUserId(
            Channel channel,
            Set<UUID> participantIds,
            Instant lastMessageAt
    ) {
        return new PrivateChannelResponseByUserId(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }

    public PublicChannelResponse toPublicChannelResponse(
            Channel channel,
            Instant lastMessageAt
    ) {
        return new PublicChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt
        );
    }
     */
}
