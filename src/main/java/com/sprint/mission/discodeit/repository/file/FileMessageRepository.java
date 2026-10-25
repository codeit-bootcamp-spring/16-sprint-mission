package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository extends FileRepository<Message> implements MessageRepository {
    private final String filePath = "./data/message.ser";

    public FileMessageRepository() {
        if (this.checkIfFileExists(filePath)) {
            System.out.printf("파일을 찾았습니다: %s, 저장된 데이터를 불러옵니다.\n".formatted(filePath));
            return;
        }
        System.out.printf("기존 파일이 없습니다. 새 데이터를 저장합니다: %s\n".formatted(filePath));
        this.writeFile(new LinkedHashMap<>(), filePath);
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
        Message message = messages.replace(id, newMessage);
        this.writeFile(messages, filePath);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Map<UUID, Message> messages = this.readFile(filePath);
        Message message = messages.get(id);
        if (message != null && !message.isDeleted()) return Optional.of(message);
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        Map<UUID, Message> messages = this.readFile(filePath);
        return messages.values().stream().filter(message -> !message.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> messages = this.readFile(filePath);
        Message message = messages.get(id);

        message.softDelete();
        this.update(id, message);
    }
}
