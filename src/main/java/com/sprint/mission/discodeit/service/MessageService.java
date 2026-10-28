package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);
    Message findById(UUID id);
    List<Message> findAll();
    List<Message> findAllByChannelId(UUID channelId);
    Message update(UUID id, UUID requesterId, String content);
    void delete(UUID id, UUID requesterId);
}
