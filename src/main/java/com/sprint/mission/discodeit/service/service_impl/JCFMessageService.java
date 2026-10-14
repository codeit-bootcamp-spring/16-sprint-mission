package com.sprint.mission.discodeit.service.service_impl;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message create(String message, UUID channelId, UUID userId) {
        Message newMessage = new Message(message, channelId, userId);
        data.put(newMessage.getId(), newMessage);
        return newMessage;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            System.out.println("존재하지 않는 메시지입니다.");
            return Optional.empty();
        }
        return Optional.of(message);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }


    @Override
    public Optional<Message> update(UUID id, String newMessage) {
        Optional<Message> message = findById(id);
        if (message.isEmpty()) {
            System.out.println("존재하지 않는 메시지의 내용을 수정할 수 없습니다.");
            return message;
        }
        message.get().setMessage(newMessage);
        message.get().setUpdatedAt(Instant.now().getEpochSecond());
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException();
        }
    }
}
