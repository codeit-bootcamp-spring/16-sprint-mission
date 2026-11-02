package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserStatusRepository implements UserStatusRepository {
    @Override
    public UserStatus save(UserStatus userStatus) {
        return null;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteByUserId(UUID userId) {

    }
}
