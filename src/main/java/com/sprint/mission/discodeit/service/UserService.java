package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto createUser(
            UserCreateRequestDto dto,
            MultipartFile profile
    ) throws IOException;

    UserDto findUser(UUID userId);

    List<UserDto> findAllUsers();

    UserResponseDto updateUser(
            UUID userId,
            UserUpdateRequestDto dto,
            MultipartFile profile
    ) throws IOException;

    void deleteUser(UUID userId);
}
