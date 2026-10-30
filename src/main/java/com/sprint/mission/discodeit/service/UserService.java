package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto createUser(UserCreateRequestDto dto);

    UserResponseDto createUser(
            UserCreateRequestDto dto,
            byte[] profileImage
    );

    UserResponseDto findUser(UUID userId);

    List<UserResponseDto> findAllUsers();

    UserResponseDto updateUser(UUID userId, UserUpdateRequestDto dto);

    UserResponseDto updateUser(
            UUID userId,
            UserUpdateRequestDto dto,
            byte[] profileImage
    );

    void deleteUser(UUID userId);
}
