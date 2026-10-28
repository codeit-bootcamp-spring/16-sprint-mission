package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name, String description);
    Channel create(ChannelType type, String name, String description);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String name, String description);
    void delete(UUID id);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}