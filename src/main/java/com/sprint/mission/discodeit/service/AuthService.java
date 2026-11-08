package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    // User login(String username, String password); DTO 사용 전
    User login(LoginRequest loginRequest);
}
