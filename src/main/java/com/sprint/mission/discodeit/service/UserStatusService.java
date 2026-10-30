package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatus create(UserStatusCreateRequest request);

    UserStatus findById(UUID id);

    List<UserStatus> findAll();

    void update(UserStatusUpdateRequest request);

    void updateByUserId(UUID userId, Instant lastActiveAt);

    void delete(UUID id);
}
