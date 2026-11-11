package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateDto dto);
    UserStatus find(UUID userStatusId);
    List<UserStatus> findAll();
    UserStatus update(UserStatusUpdateDto dto);
    UserStatus updateByUserId(UUID userId);
    void delete(UUID userStatusId);
}
