package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new LinkedHashMap<>();
    }

    @Override
    public Message save(Message message) {
        Message existingMessage = this.data.putIfAbsent(message.getId(), message);
        if (existingMessage != null) throw new IllegalStateException("이미 저장된 메세지입니다.");
        return message;
    }

    @Override
    public Message update(UUID id, Message newMessage) {
        return this.data.replace(id, newMessage);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = this.data.get(id);
        if (message != null && !message.isDeleted()) return Optional.of(message);
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return this.data.values().stream().filter(message -> !message.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        Message message = this.data.get(id);
        message.softDelete();
    }
}
