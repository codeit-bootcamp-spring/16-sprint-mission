package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new LinkedHashMap<>();
    }

    @Override
    public User create(String username, String email, String password) {
        validateUser(username, email, password, null);
        User user = new User(username, email.trim(), password);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        User user = findById(id);
        validateUser(username, email, password, id);
        user.update(username, email.trim(), password);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);

        if (!user.getMessageIds().isEmpty()) {
            throw new IllegalStateException("작성한 메시지가 남아 있어 사용자를 삭제할 수 없습니다.");
        }

        if (!user.getChannelIds().isEmpty()) {
            throw new IllegalStateException("참여 중인 채널이 있어 사용자를 삭제할 수 없습니다.");
        }

        data.remove(id);
    }

    @Override
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }

        findById(user.getId());
        validateUser(user.getUsername(), user.getEmail(), user.getPassword(), user.getId());
        data.put(user.getId(), user);
    }

    private void validateUser(String username, String email, String password, UUID excludeId) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자 이름을 입력해야 합니다.");
        }

        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("올바른 이메일을 입력해야 합니다.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해야 합니다.");
        }

        for (User user : data.values()) {
            if (!user.getId().equals(excludeId)
                    && user.getEmail().equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
    }
}