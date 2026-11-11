package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    void save(UserStatus userStatus);

    Optional<UserStatus> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    // ID로 UserStatus 조회
    Optional<UserStatus> findById(UUID id);

    // 모든 UserStatus 조회
    List<UserStatus> findAll();

    // ID로 UserStatus 삭제
    void deleteById(UUID id);
}