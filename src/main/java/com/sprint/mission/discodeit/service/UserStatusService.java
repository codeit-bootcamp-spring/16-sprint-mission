package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusResponse create(CreateUserStatusRequest request);

    UserStatusResponse findById(UUID id);

    List<UserStatusResponse> findAll();

    UserStatusResponse update(UpdateUserStatusRequest request);

    UserStatusResponse updateByUserId(
            UpdateUserStatusByUserIdRequest request
    );

    void delete(UUID id);
}