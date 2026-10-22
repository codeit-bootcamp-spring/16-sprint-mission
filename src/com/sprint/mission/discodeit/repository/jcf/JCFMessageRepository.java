package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메시지 정보는 필수입니다.");
        }

        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        data.remove(id);
    }
}
