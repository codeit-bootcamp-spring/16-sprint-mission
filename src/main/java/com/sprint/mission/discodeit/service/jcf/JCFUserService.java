package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User add(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(UUID id) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String username) {
        User user = getById(id);
        user.update(username);
        return user;
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        data.remove(id);
    }
}