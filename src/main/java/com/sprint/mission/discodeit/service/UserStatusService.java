
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatus create(UserStatusCreateRequest request);

    UserStatus find(UUID id);

    List<UserStatus> findAll();

    UserStatus update(UserStatusUpdateRequest request);

    UserStatus updateByUserId(UserStatusUpdateByUserIdRequest request);

    void delete(UUID id);
}
