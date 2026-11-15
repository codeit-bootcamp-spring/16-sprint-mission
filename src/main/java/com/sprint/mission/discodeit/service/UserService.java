package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UserService {
    UserDto create(UserCreateRequest request, MultipartFile profile)
            throws IOException;

    UserResponse find(UUID userId);

    UserListResponse findAll();

    User findUserEntity(UUID userId);

    UserStatus findUserStatus(UUID userId);

    UserDto update(UUID userId, UserUpdateRequest request, MultipartFile profile)
            throws IOException;

    void delete(UUID userId);
}
