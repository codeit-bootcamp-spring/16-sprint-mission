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
    public void add(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public User getById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, String username) {
        User user = data.get(id);
        user.update(username);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}