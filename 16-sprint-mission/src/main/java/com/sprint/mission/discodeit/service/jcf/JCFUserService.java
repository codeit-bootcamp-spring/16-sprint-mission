package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User create(String name, String password) {
        User user = new User(name, password);
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<User> findAll() {

        return data.stream().collect(Collectors.toList());
    }

    @Override
    public User update(UUID id, String name, String password) {
        User user = findById(id);
        if (user == null)
            return null;
        user.update(name, password);
        return user;
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(user -> user.getId().equals(id));

    }



}
