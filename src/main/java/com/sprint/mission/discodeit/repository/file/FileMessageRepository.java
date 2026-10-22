package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    public FileMessageRepository() {
        this.writeMessageFile(new LinkedHashMap<>());
    }

    @Override
    public Message save(Message message) {
        Map<UUID, Message> messages = this.readMessageFile();
        messages.put(message.getId(), message);
        this.writeMessageFile(messages);
        return message;
    }

    @Override
    public Message update(UUID id, Message newMessage) {
        Map<UUID, Message> messages = this.readMessageFile();
        Message message = messages.replace(id, newMessage);
        this.writeMessageFile(messages);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Map<UUID, Message> messages = this.readMessageFile();
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public List<Message> findAll() {
        Map<UUID, Message> messages = this.readMessageFile();
        return messages.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> messages = this.readMessageFile();
        Message message = messages.get(id);

        message.softDelete();
        this.update(id, message);
    }

    public Map<UUID, Message> readMessageFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./data/message.ser"))) {
            @SuppressWarnings("unchecked")
            Map<UUID, Message> messages = (Map<UUID, Message>) ois.readObject();
            return messages;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError("직렬화된 객체의 클래스를 찾을 수 없습니다.");
        }
    }

    public void writeMessageFile(Map<UUID, Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./data/message.ser"))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        }
    }
}
