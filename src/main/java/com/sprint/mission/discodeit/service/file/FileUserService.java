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
        this.store = new FileObjectStore<>(directory.resolve("users"), User.class);
    }

    @Override
    public User create(String username, String email, String password) {
        validateUser(username, email, password, null);
        return store.save(new User(username, email.trim(), password));
    }

    @Override
    public User findById(UUID id) {
        User user = store.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return store.findAll();
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        User user = findById(id);
        validateUser(username, email, password, id);
        user.update(username, email.trim(), password);
        return store.save(user);
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

        store.delete(id);
    }

    @Override
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }

        findById(user.getId());
        validateUser(user.getUsername(), user.getEmail(), user.getPassword(), user.getId());
        store.save(user);
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

        for (User user : store.findAll()) {
            if (!user.getId().equals(excludeId)
                    && user.getEmail().equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
    }
}