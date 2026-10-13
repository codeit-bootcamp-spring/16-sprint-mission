package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String message, UUID channelId, UUID userId);

    Message findById(UUID id);

    List<Message> findAll();

    void update(UUID id, String newMessage);

    Message delete(UUID id);
}
