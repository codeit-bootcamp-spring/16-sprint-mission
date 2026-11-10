package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusResponseDto createUserStatus(UserStatusCreateRequestDto dto);

    UserStatusResponseDto findUserStatus(UUID userStatusId);

    List<UserStatusResponseDto> findAllUserStatuses();

    UserStatusResponseDto updateUserStatus(UUID userStatusId);

    UserStatusResponseDto updateUserStatusByUserId(UUID userId);

    void deleteUserStatus(UUID userStatusId);
}