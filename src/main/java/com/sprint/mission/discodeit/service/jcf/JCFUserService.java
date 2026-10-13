package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final List<User> users = new ArrayList<>();

    @Override
    public User createUser(String name, int age) {
        User user = new User(name, age);
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findByName(String name) {
        return users.stream()
                .filter(user -> user.getName().equals(name))
                .toList();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User updateUser(UUID id, String name, int age) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
                );
        user.update(name, age);
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
                );
        users.remove(user);
    }
}
