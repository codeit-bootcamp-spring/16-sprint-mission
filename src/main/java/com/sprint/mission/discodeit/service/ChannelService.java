package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, UUID requesterId, String name, String description);
    void delete(UUID id, UUID requesterId);

    void join(UUID id, UUID userId);
    void leave(UUID id, UUID userId);
    void transferOwnership(UUID id, UUID currentOwnerId, UUID newOwnerId);

    void addMessage(UUID id, UUID messageId);
    void removeMessage(UUID id, UUID messageId);
}
