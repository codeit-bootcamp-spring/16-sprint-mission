package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserRequestDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserRequestDto userRequestDto);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UUID userId, UserUpdateDto userUpdateDto);
    void delete(UUID userId);
    UserResponseDto findByUsername(String username);
    UserResponseDto findByEmail(String email);
}
