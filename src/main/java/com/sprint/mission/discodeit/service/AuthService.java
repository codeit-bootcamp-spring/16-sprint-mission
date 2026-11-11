package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponseDto;

public interface AuthService {
    UserResponseDto login(LoginRequest request);
}