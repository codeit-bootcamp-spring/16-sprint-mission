package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

    UserStatus save(UserStatus entity);

    UserStatus findById(UUID id);

    List<UserStatus> findAll();

    void delete(UUID id);

    default UserStatus findByUserId(UUID userId) {
        return findAll().stream()
                .filter(status ->
                        status.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}