package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class FileUserRepository implements UserRepository {
    private static final Path PATH = Paths.get("data", "users.ser");

    @Override
    public User save(User user) {
        Map<UUID, User> data = load();          // 1. 읽기
        data.put(user.getId(), user);           // 2. 넣기 (같은 id면 덮어쓰기)
        writeToFile(data);                      // 3. 쓰기
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(load().get(id));   // 없으면 빈 상자, 예외는 안 던짐
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public boolean existsById(UUID id) {
        return load().containsKey(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return load().values().stream().
                anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return load().values().stream().
                anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> data = load();
        data.remove(id);
        writeToFile(data);
    }

    private void writeToFile(Map<UUID, User> data) {
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
    private Map<UUID, User> load() {
        if (!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
