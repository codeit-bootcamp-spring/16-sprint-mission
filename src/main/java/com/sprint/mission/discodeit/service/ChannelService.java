package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel getChannelById(UUID channelId);

    List<Channel> getChannelsByCreatorId(UUID creatorId);

    void createChannel(Channel channel);

    void updateChannelName(UUID channelId, String channelName);

    void deleteChannel(UUID channelId);
}
