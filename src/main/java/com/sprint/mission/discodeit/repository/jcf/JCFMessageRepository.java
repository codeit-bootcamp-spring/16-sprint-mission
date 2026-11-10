
package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message getById(UUID id) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        return message;
    }

    @Override
    public List<Message> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        data.remove(id);
    }
}
