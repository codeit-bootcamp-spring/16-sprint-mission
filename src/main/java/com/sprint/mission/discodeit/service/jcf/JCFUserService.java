package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    private void validateUserId(UUID id) {
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
    }

    private void validatePassword(String origin, String entered) {
        if (!origin.equals(entered)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    public boolean createUser(User user) {
        if (data.containsKey(user.getId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        data.put(user.getId(), user);
        return true;
    }

    @Override
    public boolean updateUsername(UUID id, String username) {
        validateUserId(id);

        User user = data.get(id);
        user.changeUsername(username);

        return true;
    }

    @Override
    public boolean updateEmail(UUID id, String email) {
        validateUserId(id);

        User user = data.get(id);
        user.changeEmail(email);

        return true;
    }

    @Override
    public boolean updatePassword(UUID id, String originPassword, String newPassword) {
        validateUserId(id);

        User user = data.get(id);
        validatePassword(user.getPassword(), originPassword);

        user.changePassword(newPassword);
        return true;
    }

    @Override
    public boolean deleteUser(UUID id, String password) {
        validateUserId(id);

        User user = data.get(id);
        validatePassword(user.getPassword(), password);

        data.remove(id);
        return true;
    }

    @Override
    public User getUserById(UUID id) {
        validateUserId(id);

        return data.get(id);
    }
}
