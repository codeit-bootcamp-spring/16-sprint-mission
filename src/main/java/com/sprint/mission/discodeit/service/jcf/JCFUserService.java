package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userData = new HashMap<>();

    private void validateUserId(UUID id) {
        if (!userData.containsKey(id)) {
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
        if (userData.containsKey(user.getId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        userData.put(user.getId(), user);
        return true;
    }

    @Override
    public boolean updateUsername(UUID id, String username) {
        validateUserId(id);

        User user = userData.get(id);
        user.changeUsername(username);

        return true;
    }

    @Override
    public boolean updateEmail(UUID id, String email) {
        validateUserId(id);

        User user = userData.get(id);
        user.changeEmail(email);

        return true;
    }

    @Override
    public boolean updatePassword(UUID id, String originPassword, String newPassword) {
        validateUserId(id);

        User user = userData.get(id);
        validatePassword(user.getPassword(), originPassword);

        user.changePassword(newPassword);
        return true;
    }

    @Override
    public boolean deleteUser(UUID id, String password) {
        validateUserId(id);

        User user = userData.get(id);
        validatePassword(user.getPassword(), password);

        // 해당 사용자의 메시지 확인 후 삭제
        // 해당 사용자가 만든 채널 확인
        // 해당 채널의 메시지들 삭제
        // 해당 채널 삭제
        userData.remove(id);
        return true;
    }

    @Override
    public User getUserById(UUID id) {
        validateUserId(id);

        return userData.get(id);
    }
}
