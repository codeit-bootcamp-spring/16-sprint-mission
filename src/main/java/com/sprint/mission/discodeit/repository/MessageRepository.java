package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    Message update(UUID id, Message message);

    Optional<Message> findById(UUID id);

    Optional<Message> findLastMessageOfChannel(UUID channelId);

    Optional<Message> findByAttachmentId(UUID attachmentId);

    List<Message> findAll();

    List<Message> findAllByChannelId(UUID channelId);

    void delete(UUID id);
}
