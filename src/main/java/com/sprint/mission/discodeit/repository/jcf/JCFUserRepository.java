package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(UUID userId) {
        users.remove(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, UUID userId) {
        return users.values().stream()
                .anyMatch(user ->
                        user.getUsername().equals(username)
                                && !user.getId().equals(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) {
            return false;
        }

        return users.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID userId) {
        if (email == null) {
            return false;
        }

        return users.values().stream()
                .anyMatch(user ->
                        email.equals(user.getEmail())
                                && !user.getId().equals(userId));
    }
}