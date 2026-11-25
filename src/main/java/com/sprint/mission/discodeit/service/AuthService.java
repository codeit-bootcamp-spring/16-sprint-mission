package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.dto.authentication.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
}
