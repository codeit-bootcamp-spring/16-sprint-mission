package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // User create(String username, String email, String password); DTO 사용 전
    User create(UserCreateRequest request); // DTO 사용 후
    // User find(UUID userId); // DTO 사용 전
    UserResponse find(UUID userId); // DTO 사용 후
    List<UserResponse> findAll();
    // User update(UUID userId, String newUsername, String newEmail, String newPassword); // DTO 사용 전
    UserStatus findUserStatus(UUID userId);
    User update(UUID userId, UserUpdateRequest request); // DTO 사용 후
    void delete(UUID userId);
}
