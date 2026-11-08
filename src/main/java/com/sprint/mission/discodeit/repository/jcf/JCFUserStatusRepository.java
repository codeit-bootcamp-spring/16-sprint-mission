package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    public Optional<UserStatus> findByUserId(UUID userId) {
        Optional<UserStatus> userStatus = this.findAll().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();

        return userStatus;
    }

    @Override
    public List<UserStatus> findAll() {
        return this.data.values().stream().toList(); // data.values()는 이미 존재하는 UserStatus 객체들을 반환하니까 Optional로 감쌀 이유가 없습니다.
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
