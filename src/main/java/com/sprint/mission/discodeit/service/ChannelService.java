package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel (String name, List<UUID> userIds);

    Optional<Channel> findById(UUID id);

    Optional<Channel> findByName(String name);

    List<Channel> findAll();

    List<Channel> findAllByUserId(UUID userId);

    Channel updateChannel(UUID id, String name);

    void deleteChannel(UUID id);

    void addUserToChannel(UUID id, List<UUID> userIds);

    void removeUserFromChannel(UUID id, List<UUID> userIds);

    void addMessageToChannel(UUID id, Message message);

    void removeMessageFromChannel(UUID id, Message message);
}
