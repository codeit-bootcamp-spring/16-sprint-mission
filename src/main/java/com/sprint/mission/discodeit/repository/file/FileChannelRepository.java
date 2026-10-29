package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
public class FileChannelRepository implements ChannelRepository  {
    private static final Path PATH = Paths.get("data", "channels.ser");

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = load();
        data.put(channel.getId(), channel);
        writeToFile(data);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(load().get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public boolean existsById(UUID id) {
        return load().values().stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public boolean existsName(String name) {
        return load().values().stream()
                .anyMatch(c -> c.getName().equals(name));
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Channel> data = load();
        data.remove(id);
        writeToFile(data);
    }

    private void writeToFile(Map<UUID, Channel> data) {
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
    private Map<UUID, Channel> load() {
        if (!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
