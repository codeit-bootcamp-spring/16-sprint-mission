package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(CreateUserStatusRequest request);

    UserStatusResponse find(UUID statusId);

    List<UserStatusResponse> findAll();

    UserStatusResponse update(UUID statusId, UpdateUserStatusRequest request);

    UserStatusResponse updateByUserId(UUID userId, UpdateUserStatusRequest request);

    void delete(UUID statusId);
}
