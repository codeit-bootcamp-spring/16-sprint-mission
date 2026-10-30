package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.FileObjectStore;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    private final FileObjectStore<User> store;

    public FileUserService(Path directory) {
        this.store = new FileObjectStore<>(
                directory.resolve("users"),
                User.class
        );
    }

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

        return store.save(user);
    }

    @Override
    public User findById(UUID id) {
        User user = store.findById(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return store.findAll();
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

        return store.save(user);
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

        store.delete(id);
    }

    @Override
    public void save(User user) {
        findById(user.getId());
        store.save(user);
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

        for (User user : store.findAll()) {
            if (!user.getId().equals(id)
                    && user.getEmail().equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException(
                        "중복 이메일입니다."
                );
            }
        }
    }
}