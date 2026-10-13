package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService extends BaseEntityService<Channel> {
    Channel create(String name);
}
