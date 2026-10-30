package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChannelService {

    Channel create(String name, String description);

    Channel create(
            ChannelType type,
            String name,
            String description
    );

    Channel findById(UUID id);

    List<Channel> findAll();

    Channel update(
            UUID id,
            String name,
            String description
    );

    void delete(UUID id);

    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);

    default ChannelResponse createPublic(
            CreatePublicChannelRequest request
    ) {
        Channel channel = create(
                ChannelType.PUBLIC,
                request.name(),
                request.description()
        );

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                null,
                Set.of()
        );
    }

    default ChannelResponse createPrivate(
            CreatePrivateChannelRequest request
    ) {
        throw new UnsupportedOperationException(
                "PRIVATE 채널은 BasicChannelService를 사용하세요."
        );
    }

    default ChannelResponse findResponseById(UUID id) {
        Channel channel = findById(id);

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                null,
                channel.getType() == ChannelType.PRIVATE
                        ? Set.copyOf(channel.getMemberIds())
                        : Set.of()
        );
    }

    default List<ChannelResponse> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || channel.getMemberIds().contains(userId))
                .map(channel -> findResponseById(channel.getId()))
                .toList();
    }

    default ChannelResponse update(UpdateChannelRequest request) {
        update(
                request.id(),
                request.name(),
                request.description()
        );

        return findResponseById(request.id());
    }
}