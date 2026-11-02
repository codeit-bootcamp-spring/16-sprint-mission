package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto create(UserStatusCreateRequest request);
    Optional<UserStatusDto> findById(UUID id);
    List<UserStatusDto> findAll();
    UserStatusDto update(UserStatusUpdateRequest request);
    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);
    void delete(UUID id);
}
