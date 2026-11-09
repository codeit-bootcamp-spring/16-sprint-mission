package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse create(
            CreateUserRequest request,
            MultipartFile profile
    );

    UserResponse findById(UUID id);

    List<UserResponse> findAll();

    UserResponse update(UpdateUserRequest request);

    void delete(UUID id);
}