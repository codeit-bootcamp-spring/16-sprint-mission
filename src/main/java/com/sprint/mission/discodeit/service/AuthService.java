package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;

public interface AuthService {
    UserDto login(UserLoginRequest request);
}
