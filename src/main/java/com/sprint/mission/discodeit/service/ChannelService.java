package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface ChannelService extends BaseEntityService<Channel> {
    Channel create(String name);

    Channel update(UUID id, String name);

    void addUser(UUID id, User user);

    void removeUser(UUID channelId, UUID userId);
}
