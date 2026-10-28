package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final File file;

    public FileUserRepository() {
        this.file = new File("data/users.dat");

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }

        Map<UUID, User> data = readAllAsMap();
        data.put(user.getId(), user);
        writeAll(data);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(readAllAsMap().get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(readAllAsMap().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> data = readAllAsMap();

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        data.remove(id);
        writeAll(data);
    }

    private Map<UUID, User> readAllAsMap() {
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("사용자 파일을 읽을 수 없습니다.", e);
        }
    }

    private void writeAll(Map<UUID, User> data) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException("사용자 파일을 저장할 수 없습니다.", e);
        }
    }
}
