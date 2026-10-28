package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepository {
    private final File file = new File("data/messages.ser");

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메세지 읽기 실패", e);
        }

    }
    private void saveAll(Map<UUID, Message> data) {
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("메세지 저장 실패", e);
        }
    }

    @Override
    public Message save(Message message) {
        Map<UUID, Message> data = load();
        data.put(message.getId(), message);
        saveAll(data);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return load().get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Message> data = load();
        data.remove(id);
        saveAll(data);
    }

    @Override
    public List<Message> findByContent(String keyword) {
        return load().values().stream()
                .filter(message -> message.getContent().contains(keyword))
                .collect(Collectors.toList());
    }

}