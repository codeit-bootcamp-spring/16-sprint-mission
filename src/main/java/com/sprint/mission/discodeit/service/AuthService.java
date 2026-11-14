package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthUserDto;
import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequest;

public interface AuthService {

    // 로그인 기능
    AuthUserDto login(AuthLoginRequest request);
}