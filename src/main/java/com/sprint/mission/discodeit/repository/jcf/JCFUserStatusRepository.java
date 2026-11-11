package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        UserStatus existing = this.data.putIfAbsent(userStatus.getId(), userStatus);
        if (existing != null) throw new IllegalStateException("이미 저장된 유저 접속 기록입니다.");
        return userStatus;
    }

    @Override
    public UserStatus update(UUID id, UserStatus userStatus) {
        UserStatus existing = this.data.replace(id, userStatus);
        if (existing == null) throw new IllegalStateException("저장되지 않은 유저 접속 기록입니다.");
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        UserStatus userStatus = this.data.get(id);
        if (userStatus != null && !userStatus.isDeleted()) return Optional.of(userStatus);
        return Optional.empty();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return this.data.values()
                .stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .filter(userStatus -> !userStatus.isDeleted())
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return this.data.values()
                .stream()
                .filter(userStatus -> !userStatus.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = this.data.get(id);
        if (userStatus == null || userStatus.isDeleted())
            throw new IllegalArgumentException("삭제할 유저 접속 기록이 존재하지 않습니다.");
        userStatus.softDelete();
    }
}
