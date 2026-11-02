package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class FileUserStatusRepository implements UserStatusRepository {
    private static final Path PATH = Paths.get("data", "userStatuses.ser");

    @Override
    public UserStatus save(UserStatus userStatus) {
        Map<UUID, UserStatus> data = load();          // 1. 읽기
        data.put(userStatus.getId(), userStatus);           // 2. 넣기 (같은 id면 덮어쓰기)
        writeToFile(data);                      // 3. 쓰기
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(load().get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return load().values().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, UserStatus> data = load();
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> data = load();
        data.values().removeIf(us -> us.getUserId().equals(userId));
        writeToFile(data);
    }

    private void writeToFile(Map<UUID, UserStatus> data) {
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
    private Map<UUID, UserStatus> load() {
        if (!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
