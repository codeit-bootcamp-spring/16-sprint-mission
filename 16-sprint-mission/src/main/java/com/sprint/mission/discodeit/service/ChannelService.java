package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

public interface ChannelService extends ChannelUpdate {
    Channel create(ChannelType channelType, String title, String description);

    Channel read(String id) throws Exception;

    Channel delete(String id) throws Exception;
}
