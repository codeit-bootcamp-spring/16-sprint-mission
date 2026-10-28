package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message findById(UUID messageId);
    List<Message> findAll();
    Message save(Message message);
    void deleteById(UUID messageId);

    List<Message> findByUserId(UUID userId);
    List<Message> findByChannelId(UUID channelId);
    List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId);
}
