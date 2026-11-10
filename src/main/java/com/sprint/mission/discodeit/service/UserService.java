package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(CreateUserRequest dto);
    UserResponse find(UUID userId);
    List<UserResponse> findAll();
    UserResponse update(UUID id, UpdateUserRequest dto);
    void delete(UUID userId);
}
