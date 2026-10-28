package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository extends FileRepository<User> implements UserRepository {
    private final String filePath = "./data/user.ser";

    public FileUserRepository() {
        if (this.checkIfFileExists(filePath)) {
            System.out.printf("파일을 찾았습니다: %s, 저장된 데이터를 불러옵니다.\n".formatted(filePath));
            return;
        }
        System.out.printf("기존 파일이 없습니다. 새 데이터를 저장합니다: %s\n".formatted(filePath));
        this.writeFile(new HashMap<>(), filePath);
    }

    @Override
    public User save(User user) {
        Map<UUID, User> users = this.readFile(filePath);
        User existingUser = users.putIfAbsent(user.getId(), user);
        if (existingUser != null) throw new IllegalStateException("이미 저장된 유저입니다.");
        this.writeFile(users, filePath);
        return user;
    }

    @Override
    public User update(UUID id, User newUser) {
        Map<UUID, User> users = this.readFile(filePath);
        User user = users.replace(id, newUser);
        this.writeFile(users, filePath);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Map<UUID, User> users = this.readFile(filePath);
        User user = users.get(id);
        if (user != null && !user.isDeleted()) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        Map<UUID, User> users = this.readFile(filePath);
        return users.values().stream().filter(user -> !user.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> users = this.readFile(filePath);
        User user = users.get(id);
        user.softDelete();
        this.update(id, user);
    }
}

