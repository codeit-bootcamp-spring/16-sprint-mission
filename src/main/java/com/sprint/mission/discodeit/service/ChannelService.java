package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName);
    List<Channel> findAll();
    Channel findById(UUID id);
    void updateChannelName(UUID id, String channelName);
    boolean deleteChannel(UUID id);
}
