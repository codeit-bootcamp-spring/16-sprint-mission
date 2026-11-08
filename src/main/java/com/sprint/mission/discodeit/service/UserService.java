package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto createUser(UserCreateRequestDto dto);

    UserDto findUser(UUID userId);

    List<UserDto> findAllUsers();

    UserDto updateUser(UUID userId, UserUpdateRequestDto dto);

    void deleteUser(UUID userId);
}
