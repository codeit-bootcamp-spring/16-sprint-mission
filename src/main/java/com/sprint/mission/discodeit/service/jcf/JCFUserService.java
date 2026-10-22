package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserChannelManager;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private UserChannelManager userChannelManager;

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
        user.validateNotDeleted();
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
        this.userChannelManager.deleteUser(id);
    }

    public void setUserChannelManager(UserChannelManager userChannelManager) {
        this.userChannelManager = userChannelManager;
    }
}
