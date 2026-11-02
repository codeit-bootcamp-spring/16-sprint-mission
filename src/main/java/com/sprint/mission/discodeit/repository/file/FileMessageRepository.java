package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository extends FileRepository<Message> implements MessageRepository {
    public FileMessageRepository() {
        super("./data/mesage.ser", new LinkedHashMap<>());
    }

    @Override
    public Message save(Message message) {
        Map<UUID, Message> messages = this.readFile(filePath);
        Message existingMessage = messages.putIfAbsent(message.getId(), message);
        if (existingMessage != null) throw new IllegalStateException("이미 저장된 메세지입니다.");
        this.writeFile(messages, filePath);
        return message;
    }

    @Override
    public Message update(UUID id, Message newMessage) {
        Map<UUID, Message> messages = this.readFile(filePath);
        Message existing = messages.replace(id, newMessage);
        if (existing == null) throw new IllegalStateException("저장되지 않은 메세지입니다.");
        this.writeFile(messages, filePath);
        return newMessage;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Map<UUID, Message> messages = this.readFile(filePath);
        Message message = messages.get(id);
        if (message != null && !message.isDeleted()) return Optional.of(message);
        return Optional.empty();
    }

    @Override
    public Optional<Message> findLastMessageOfChannel(UUID channelId) {
        Map<UUID, Message> messages = this.readFile(filePath);
        List<Message> availableMessages = messages.values()
                .stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .filter(message -> !message.isDeleted())
                .toList();
        if (availableMessages.isEmpty()) return Optional.empty();
        return Optional.of(availableMessages.get(availableMessages.size() - 1));
    }

    @Override
    public List<Message> findAll() {
        Map<UUID, Message> messages = this.readFile(filePath);
        return messages.values().stream().filter(message -> !message.isDeleted()).toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Map<UUID, Message> messages = this.readFile(filePath);
        return messages.values()
                .stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .filter(message -> !message.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> messages = this.readFile(filePath);
        Message message = messages.get(id);
        if (message == null || message.isDeleted()) throw new IllegalArgumentException("삭제할 메세지가 존재하지 않습니다.");
        message.softDelete();
        this.writeFile(messages, filePath);
    }
}
