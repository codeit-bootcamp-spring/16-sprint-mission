package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    Optional<UserStatus> findById(UUID userStatusId);
    Optional<UserStatus> findByUserId(UUID userId);
    List<UserStatus> findAll();
    UserStatus save(UserStatus userStatus);
    void deleteById(UUID userStatusId);
}
