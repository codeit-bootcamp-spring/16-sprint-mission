package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService extends ChannelUpdate {
    Channel create(ChannelType channelType, String title, String description);

    Channel findById(UUID id);

    List<Channel> findAll();

    Channel delete(UUID id);
}
