package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String channelName, String topic, String type, String serverId);
    Channel read(UUID id);
    List<Channel> readAll();
    Channel update(UUID id, String channelName, String topic, String type, String serverId);
    void addParticipant(UUID channelId, UUID userId);
    void removeParticipant(UUID channelId, UUID userId);
    void delete(UUID id);
}
