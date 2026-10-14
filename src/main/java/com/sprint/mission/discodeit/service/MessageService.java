package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String comment, UUID userId, UUID channelId);
    Message findById(UUID id);
    List<Message> findAll();
    Message update(UUID id, String comment);
    void delete(UUID id);
}
