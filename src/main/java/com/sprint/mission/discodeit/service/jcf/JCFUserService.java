package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
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

    public User update(UUID id, String name) {
        User user = this.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        return user.update(name);
    }

    public User update(UUID id, Map<UUID, Channel> channels) {
        User user = this.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        return user.update(channels);
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
    public User delete(UUID id) {
        this.findById(id).orElseThrow(() -> new IllegalArgumentException("삭제할 유저가 존재하지 않습니다."));
        return this.data.remove(id);
    }
}
