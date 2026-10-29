package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDto create(UserCreateRequest request, Optional<BinaryContentRequest> profileRequest);
    UserDto findById(UUID id);
    List<UserDto> findAll();
    List<UserDto> findAllByChannelId(UUID channelId);
    UserDto update(UUID id, UserCreateRequest request, Optional<BinaryContentRequest> profileRequest);
    void delete(UUID id);
}
