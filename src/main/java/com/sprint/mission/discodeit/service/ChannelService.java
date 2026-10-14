package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, UUID requesterId);
    void delete(UUID id, UUID requesterId);

    List<User> findMember(UUID id);
    void joinChannel(UUID id, UUID userId);
    void leaveChannel(UUID id, UUID userId);
    void transformOwnership(UUID id, UUID currentOwnerId, UUID newOwnerId);
}
