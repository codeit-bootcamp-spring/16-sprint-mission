package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository extends FileRepository<User> implements UserRepository {
    public FileUserRepository() {
        super("user.ser", new HashMap<>());
    }

    @Override
    public User save(User user) {
        Map<UUID, User> users = this.readFile(resolvedPath);
        User existingUser = users.putIfAbsent(user.getId(), user);
        if (existingUser != null) throw new IllegalStateException("이미 저장된 유저입니다.");
        this.writeFile(users, resolvedPath);
        return user;
    }

    @Override
    public User update(UUID id, User newUser) {
        Map<UUID, User> users = this.readFile(resolvedPath);
        User existing = users.replace(id, newUser);
        if (existing == null) throw new IllegalStateException("저장되지 않은 유저입니다.");
        this.writeFile(users, resolvedPath);
        return newUser;
    }

    @Override
    public Optional<User> findByName(String name) {
        Map<UUID, User> users = this.readFile(resolvedPath);
        return users.values()
                .stream()
                .filter(user -> user.getName().equals(name))
                .filter(user -> !user.isDeleted())
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Map<UUID, User> users = this.readFile(resolvedPath);
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .filter(user -> !user.isDeleted())
                .findFirst();
    }

    @Override
    public Optional<User> findById(UUID id) {
        Map<UUID, User> users = this.readFile(resolvedPath);
        User user = users.get(id);
        if (user != null && !user.isDeleted()) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public Optional<User> findByProfileId(UUID profileId) {
        Map<UUID, User> users = this.readFile(resolvedPath);
        return users.values()
                .stream()
                .filter(user -> user.getProfileId().equals(profileId))
                .filter(user -> !user.isDeleted())
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        Map<UUID, User> users = this.readFile(resolvedPath);
        return users.values().stream().filter(user -> !user.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> users = this.readFile(resolvedPath);
        User user = users.get(id);
        if (user == null || user.isDeleted()) throw new IllegalArgumentException("삭제할 유저가 존재하지 않습니다.");
        user.softDelete();
        this.writeFile(users, resolvedPath);
    }
}

