package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest request);

    UserDto create(UserCreateRequest request, MultipartFile profile);

    Optional<UserDto> findById(UUID id);

    List<UserDto> findAll();

    UserDto update(
            UUID userId,
            UserUpdateRequest request,
            MultipartFile profile
    );

    void deleteById(UUID id);

    void addChannel(UUID userId, UUID channelId);

    void removeChannel(UUID userId, UUID channelId);
}