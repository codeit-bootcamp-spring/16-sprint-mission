package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends SaveObject<Channel> {
    List<Channel> getChannels();
    Channel getChannel(UUID id);
    void removeChannel(UUID id);
}
