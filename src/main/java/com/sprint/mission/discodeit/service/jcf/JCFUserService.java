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
    public User update(UUID id, User newUser) {
        User user = this.findById(id).orElseThrow(() -> new NoSuchElementException("수정할 유저가 존재하지 않습니다."));
        this.validateNotDeleted(user);
        user.update(newUser);
        this.data.put(id, user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        User user = this.findById(id).orElseThrow(() -> new NoSuchElementException("삭제할 유저가 존재하지 않습니다."));
        this.validateNotDeleted(user);

        // 유저 삭제 시 가입된 채널들에서도 유저 탈퇴
        user.getChannels().values().forEach(channel -> channel.removeUser(id));

        user.softDelete();
    }

    public void validateNotDeleted(User user) {
        if (user.isDeleted()) throw new IllegalStateException("삭제된 유저입니다.");
    }
}
