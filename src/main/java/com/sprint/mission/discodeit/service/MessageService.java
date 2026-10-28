package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message add(Message message);

    Message getById(UUID id);

    List<Message> getAll();

    Message update(UUID id, String content);

    void delete(UUID id);
}