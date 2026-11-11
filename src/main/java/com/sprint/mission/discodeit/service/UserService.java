package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateDto createDto);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    User update(UUID userId, UserUpdateDto updateDto);
    void delete(UUID userId);
}
