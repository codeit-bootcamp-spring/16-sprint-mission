package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType channelType, String title, String description);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    Optional<Channel> updateChannelType(UUID id, ChannelType newChannelType);

    Optional<Channel> updateTitle(UUID id, String newTitle);

    Optional<Channel> updateDescription(UUID id, String newDescription);

    void delete(UUID id);
}
