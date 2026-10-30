package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data = new LinkedHashMap<>();

    @Override
    public User create(
            String username,
            String email,
            String password
    ) {
        validate(username, email, password, null);

        User user = new User(
                username,
                email.trim(),
                password
        );

        data.put(user.getId(), user);

        return user;
    }

    @Override
    public User findById(UUID id) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(
            UUID id,
            String username,
            String email,
            String password
    ) {
        User user = findById(id);

        validate(username, email, password, id);

        user.update(
                username,
                email.trim(),
                password
        );

        data.put(id, user);

        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);

        if (!user.getChannelIds().isEmpty()
                || !user.getMessageIds().isEmpty()) {
            throw new IllegalStateException(
                    "관계가 남아 있습니다."
            );
        }

        data.remove(id);
    }

    @Override
    public void save(User user) {
        findById(user.getId());

        data.put(user.getId(), user);
    }

    private void validate(
            String username,
            String email,
            String password,
            UUID id
    ) {
        if (username == null
                || username.isBlank()
                || email == null
                || !email.contains("@")
                || password == null
                || password.isBlank()) {
            throw new IllegalArgumentException(
                    "사용자 정보가 올바르지 않습니다."
            );
        }

        for (User user : data.values()) {
            if (!user.getId().equals(id)
                    && user.getEmail().equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException(
                        "중복 이메일입니다."
                );
            }
        }
    }
}