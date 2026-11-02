package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
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
        User existing = this.data.replace(id, newUser);
        if (existing == null) throw new IllegalStateException("저장되지 않은 유저입니다.");
        return newUser;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = this.data.get(id);
        if (user != null && !user.isDeleted()) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public Optional<User> findByName(String name) {
        return this.data.values()
                .stream()
                .filter(user -> user.getName().equals(name))
                .filter(user -> !user.isDeleted())
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.data.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .filter(user -> !user.isDeleted())
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().filter(user -> !user.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        User user = this.data.get(id);
        if (user == null || user.isDeleted()) throw new IllegalArgumentException("삭제할 유저가 존재하지 않습니다.");
        user.softDelete();
    }
}
