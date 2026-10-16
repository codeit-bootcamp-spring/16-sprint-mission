package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Message create(String content, UUID authorId, UUID channelId);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    Message update(UUID id, String content);

    void delete(UUID id);
}