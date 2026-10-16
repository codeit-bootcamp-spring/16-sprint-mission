package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message create(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    Message update(Message message);

    void delete(UUID id);
}
