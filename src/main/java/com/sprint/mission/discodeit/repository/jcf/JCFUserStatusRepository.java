package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

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
    public Optional<UserStatus> find(UUID userStatusId) {
        return Optional.ofNullable(this.data.get(userStatusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return this.data.values().stream().filter(userStatus -> userStatus.getUserId().equals(userId)).findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
