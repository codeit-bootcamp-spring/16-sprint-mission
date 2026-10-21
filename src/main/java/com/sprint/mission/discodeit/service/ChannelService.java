package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);

    Channel update(UUID id, Channel channel);

    void addUserToChannel(UUID channelId, User user);

    void removeUserFromChannel(UUID channelId, UUID userId);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    void delete(UUID id);

    void validateNotDeleted(Channel channel);
}
