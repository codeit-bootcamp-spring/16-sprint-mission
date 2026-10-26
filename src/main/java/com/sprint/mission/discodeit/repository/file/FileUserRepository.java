package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final Path filePath = Paths.get("data", "users.dat");
    private final Map<UUID, User> data;

    public FileUserRepository() {
        this.data = loadData();
    }

    // 파일에서 사용자 데이터 불러오기
    private Map<UUID, User> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, User>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("사용자 데이터를 불러오지 못했습니다.", e);
        }
    }

    // 사용자 데이터를 파일에 저장
    private void saveData() {
        try {
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream output =
                         new ObjectOutputStream(Files.newOutputStream(filePath))) {
                output.writeObject(data);
            }
        } catch (IOException e) {
            throw new IllegalStateException("사용자 데이터를 저장하지 못했습니다.", e);
        }
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
        saveData();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveData();
    }
}

