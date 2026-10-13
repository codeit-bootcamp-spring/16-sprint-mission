package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final List<User> data = new ArrayList<>();

    @Override
    public User createUser(String name, int age) {
        User user = new User(name, age);
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findByName(String name) {
        return data.stream()
                .filter(user -> user.getName().equals(name))
                .toList();
    }

    @Override
    public List<User> findAll() {
        return data.stream()
                .toList();
    }

    @Override
    public User updateUser(UUID id, String name, int age) {
        Optional<User> optionalUser = findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 ID 입니다.");
        }
        User user = optionalUser.get();
        user.update(name, age);
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        Optional<User> optionalUser = findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 ID 입니다.");
        }
        optionalUser.ifPresent(data::remove);
    }
}
