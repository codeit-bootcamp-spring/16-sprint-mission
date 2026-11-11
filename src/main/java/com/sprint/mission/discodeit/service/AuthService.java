package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.LoginResponse;


public interface AuthService {
    LoginResponse login(LoginRequest request);
}
