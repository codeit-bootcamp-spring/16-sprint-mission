package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.LoginFailedException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findAll().stream().filter(u->u.getName().equals(request.name())&&u.getPassword().equals(request.password())).findFirst().orElseThrow(() ->
                new LoginFailedException("이름 또는 비밀번호가 일치하지 않습니다"));

        return new LoginResponse(user.getId(), user.getName(), user.getEmail(), user.getProfileId());
    }

}
