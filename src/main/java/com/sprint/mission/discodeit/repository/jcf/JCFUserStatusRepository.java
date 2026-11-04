package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return this.data.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findByUserIdIn(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyList();
        return this.data.values().stream()
                .filter(s -> userIds.contains(s.getUserId()))
                .toList();
    }

    @Override
    public List<UserStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UserStatus userStatus) {
        if (userStatus != null) {
            this.data.remove(userStatus.getId());
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        this.data.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst()
                .ifPresent(s -> this.data.remove(s.getId()));
    }
}
