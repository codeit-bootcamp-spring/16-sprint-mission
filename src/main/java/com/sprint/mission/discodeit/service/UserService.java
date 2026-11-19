package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.response.FindUserResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(BinaryContentRequest profile, CreateUserRequest userRequest);
    UserResponse find(UUID userId);
    List<FindUserResponse> findAll();
    UserResponse update(UUID id, BinaryContentRequest profile, UpdateUserRequest request);
    void delete(UUID userId);
}
