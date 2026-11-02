package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
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
        Message existing = this.data.replace(id, newMessage);
        if (existing == null) throw new IllegalStateException("저장되지 않은 메세지입니다.");
        return newMessage;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = this.data.get(id);
        if (message != null && !message.isDeleted()) return Optional.of(message);
        return Optional.empty();
    }

    @Override
    public Optional<Message> findLastMessageOfChannel(UUID channelId) {
        return this.data.values()
                .stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .filter(message -> !message.isDeleted())
                .max(Comparator.comparing(Message::getCreatedAt));
    }

    @Override
    public Optional<Message> findByAttachmentId(UUID attachmentId) {
        return this.data.values()
                .stream()
                .filter(message -> message.getAttachmentIds().contains(attachmentId))
                .filter(message -> !message.isDeleted())
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return this.data.values().stream().filter(message -> !message.isDeleted()).toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return this.data.values()
                .stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .filter(message -> !message.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Message message = this.data.get(id);
        if (message == null || message.isDeleted()) throw new IllegalArgumentException("삭제할 메세지가 존재하지 않습니다.");
        message.softDelete();
    }
}
