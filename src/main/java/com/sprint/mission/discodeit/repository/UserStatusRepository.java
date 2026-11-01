package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    public UserStatus save(UserStatus userStatus);
    public Optional<UserStatus> find(UUID userStatusId);
    public Optional<UserStatus> findByUserId(UUID userId);
    public List<UserStatus> findAll(UUID userId);
    public void deleteById(UUID id);
}
