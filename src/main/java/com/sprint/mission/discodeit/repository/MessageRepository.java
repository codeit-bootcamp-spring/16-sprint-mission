
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void save(Message message);

    Message getById(UUID id);

    List<Message> getAll();

    void delete(UUID id);
}
