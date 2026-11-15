package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {

    UserResponseDto login(AuthLoginRequestDto dto);
}
