package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class FileReadStatusRepository implements ReadStatusRepository {
    private static final Path PATH = Paths.get("data", "readStatuses.ser");

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> data = load();          // 1. 읽기
        data.put(readStatus.getId(), readStatus);           // 2. 넣기 (같은 id면 덮어쓰기)
        writeToFile(data);                      // 3. 쓰기
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(load().get(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return load().values().stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return load().values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return load().values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, ReadStatus> data = load();
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        Map<UUID, ReadStatus> data = load();
        data.values().removeIf(rs -> rs.getUserId().equals(userId));
        writeToFile(data);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> data = load();
        data.values().removeIf(rs -> rs.getChannelId().equals(channelId));
        writeToFile(data);
    }

    private void writeToFile(Map<UUID, ReadStatus> data) {
        try {
            Files.createDirectories(PATH.getParent());
            try (ObjectOutputStream oos =
                         new ObjectOutputStream(Files.newOutputStream(PATH))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> load() {
        if (!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
