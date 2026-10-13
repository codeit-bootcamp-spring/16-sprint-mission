package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        this.data.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = this.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        return oldUser.update(user.getName());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<List<User>> findAll() {
        return Optional.of(this.data.values().stream().toList());
    }

    @Override
    public void delete(UUID id) {
        this.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        this.data.remove(id);
    }
}
