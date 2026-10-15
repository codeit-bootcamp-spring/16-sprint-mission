package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);

    Channel updateName(UUID id, String name);

    void addUserToChannel(UUID channelId, User user);

    void removeUserFromChannel(UUID channelId, UUID userId);

    Channel findById(UUID id);

    List<Channel> findAll();

    void delete(UUID id);
}
