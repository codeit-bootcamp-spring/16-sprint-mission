package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }


    @Override
    public User create(User user) {
        isValid(user);

        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, User user) {
        if (!data.containsKey(id)) {
            return null;
        }

        isValid(user);
        data.put(id, user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    private void isValid(User user) {
        for (User value : data.values()) {
            if (value.getEmail().equals(user.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
    }
}
