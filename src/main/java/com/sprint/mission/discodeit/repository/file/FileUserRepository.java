package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {
    private final FileStore<User> store = new FileStore<>("users.ser");

    @Override
    public User save(User user) {
        Map<UUID, User> data = store.load();          // 1. 읽기
        data.put(user.getId(), user);           // 2. 넣기 (같은 id면 덮어쓰기)
        store.write(data);                      // 3. 쓰기
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(store.load().get(id));   // 없으면 빈 상자, 예외는 안 던짐
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return store.load().values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.load().values());
    }

    @Override
    public boolean existsById(UUID id) {
        return store.load().containsKey(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return store.load().values().stream().
                anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.load().values().stream().
                anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> data = store.load();
        data.remove(id);
        store.write(data);
    }
}
