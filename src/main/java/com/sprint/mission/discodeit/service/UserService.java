package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest request, MultipartFile profile);
    UserResponse find(UUID userId);
    List<UserResponse> findAll();
    UserStatus findUserStatus(UUID userId);
    User update(UUID userId, UserUpdateRequest request, MultipartFile profile);
    void delete(UUID userId);
}
