package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {

    UserStatus create(UserStatusCreateRequest request);

    Optional<UserStatus> findById(UUID id);

    List<UserStatus> findAll();

    UserStatus update(UserStatusUpdateRequest request);

    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);

    void delete(UUID id);
}