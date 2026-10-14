package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    void add(Message message);
    Message getById(UUID id);
    List<Message> getAll();
    void delete(UUID id);
    void update(UUID id, String content);
}
