package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {
    private final FileStore<UserStatus> store = new FileStore<>("userStatuses.ser");

    @Override
    public UserStatus save(UserStatus userStatus) {
        Map<UUID, UserStatus> data = store.load();          // 1. 읽기
        data.put(userStatus.getId(), userStatus);           // 2. 넣기 (같은 id면 덮어쓰기)
        store.write(data);                      // 3. 쓰기
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(store.load().get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return store.load().values().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(store.load().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, UserStatus> data = store.load();
        data.remove(id);
        store.write(data);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> data = store.load();
        data.values().removeIf(us -> us.getUserId().equals(userId));
        store.write(data);
    }
}
