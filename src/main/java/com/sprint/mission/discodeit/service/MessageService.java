package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(String message, UUID channelId, UUID userId);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    Optional<Message> update(UUID id, String newMessage);

    void delete(UUID id);
}
