package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;

public interface AuthService {

    UserDto login(AuthLoginRequestDto dto);
}
