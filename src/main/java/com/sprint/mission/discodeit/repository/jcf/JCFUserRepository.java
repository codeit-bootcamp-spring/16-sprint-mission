package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        User existingUser = this.data.putIfAbsent(user.getId(), user);
        if (existingUser != null) throw new IllegalStateException("이미 저장된 유저입니다.");
        return user;
    }

    @Override
    public User update(UUID id, User newUser) {
        return this.data.replace(id, newUser);
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = this.data.get(id);
        if (user != null && !user.isDeleted()) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().filter(user -> !user.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        User user = this.data.get(id);
        user.softDelete();
    }
}
