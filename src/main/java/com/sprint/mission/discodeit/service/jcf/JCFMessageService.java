package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message add(Message message) {
        data.put(message.getId(), message);
        return message;
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
    public Message update(UUID id, String content) {
        Message message = getById(id);
        message.update(content);
        return message;
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        data.remove(id);
    }
}