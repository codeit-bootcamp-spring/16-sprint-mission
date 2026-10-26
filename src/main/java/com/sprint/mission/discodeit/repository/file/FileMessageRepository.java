package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private final Path filePath = Paths.get("data", "messages.dat");
    private final Map<UUID, Message> data;

    public FileMessageRepository() {
        this.data = loadData();
    }

    private Map<UUID, Message> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, Message>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("메시지 데이터를 불러오지 못했습니다.", e);
        }
    }

    private void saveData() {
        try {
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream output =
                         new ObjectOutputStream(Files.newOutputStream(filePath))) {
                output.writeObject(data);
            }
        } catch (IOException e) {
            throw new IllegalStateException("메시지 데이터를 저장하지 못했습니다.", e);
        }
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
        saveData();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveData();
    }
}

