package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
public class FileMessageRepository implements MessageRepository {
    private static final Path PATH = Paths.get("data", "messages.ser");

    @Override
    public Message save(Message message) {
        Map<UUID, Message> data = load();
        data.put(message.getId(), message);
        writeToFile(data);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(load().get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return load().values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findAllByUserId(UUID userId) {
        return load().values().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return load().containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Message> data = load();
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        Map<UUID, Message> data = load();
        data.values().removeIf(message -> message.getAuthorId().equals(userId));
        writeToFile(data);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        Map<UUID, Message> data = load();
        data.values().removeIf(message -> message.getChannelId().equals(channelId));
        writeToFile(data);
    }

    private void writeToFile(Map<UUID, Message> data) {
        try {
            Files.createDirectories(PATH.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(PATH))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> load() {
        if(!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
