package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusCreateRequest request);
    UserStatusResponse find(UUID userStatusId);
    List<UserStatusResponse> findAll();
    UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request);
    UserStatusResponse updateByUserId(UUID userId);
    void delete(UUID userStatusId);
}
