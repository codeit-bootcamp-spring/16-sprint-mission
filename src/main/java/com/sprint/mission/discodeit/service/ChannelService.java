package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    boolean createChannel(Channel channel);

    boolean updateChannelName(UUID channelId, String channelName);

    boolean deleteChannel(UUID channelId);

    void getChannelById(UUID channelId);

    List<Channel> getChannelsByUserId(UUID userId);
}
