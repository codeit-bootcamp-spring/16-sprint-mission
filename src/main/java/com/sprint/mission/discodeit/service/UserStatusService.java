package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatus create(CreateUserStatusRequest request);

    UserStatus findById(UUID id);

    List<UserStatus> findAll();

    UserStatus update(UpdateUserStatusRequest request);

    UserStatus updateByUserId(
            UpdateUserStatusByUserIdRequest request
    );

    void delete(UUID id);
}