
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserDto;

public interface AuthService {

    // 로그인
    UserDto login(LoginRequest request);
}
