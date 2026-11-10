package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserCreateRequest request, MultipartFile profile)
            throws IOException;

    UserResponse find(UUID userId);

    List<UserResponse> findAll();

    UserStatus findUserStatus(UUID userId);

    UserResponse update(UUID userId, UserUpdateRequest request, MultipartFile profile)
            throws IOException;

    void delete(UUID userId);
}
