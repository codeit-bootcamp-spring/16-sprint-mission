package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;

public interface AuthService {

    UserResponseDto login(AuthLoginRequestDto dto);
}
