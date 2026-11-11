package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.AuthUserDto;

public interface AuthService {

    AuthUserDto login(AuthLoginRequest request);
}