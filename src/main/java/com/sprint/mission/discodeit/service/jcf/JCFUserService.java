package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;


    public JCFUserService() {
        this.data = new HashMap<>();
    }
    @Override
    public User create(String username, String password, String phoneNumber, String email) {
        User user = new User(username, password, phoneNumber, email);
        data.put(user.getId(), user);
        return user;
    }
    @Override
    public User read(UUID id) {
        return data.get(id);
    }
    @Override
    public List<User> readAll() {
        return new ArrayList<>(data.values());
    }
    @Override
    public User update(UUID id, String username, String password, String phoneNumber, String email) {
       User user = data.get(id);
       user.update(username, password, phoneNumber, email);
        return user;
    }
@Override
    public void delete(UUID id) {
        data.remove(id);
}
}