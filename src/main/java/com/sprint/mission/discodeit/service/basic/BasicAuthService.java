package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.dto.authentication.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    private final AuthMapper authMapper;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        /*
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(loginRequest.username())
                        && u.getPassword().equals(loginRequest.password()))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialException(
                        "입력하신 ID 또는 Password 가 올바르지 않습니다."
                ));
         */

        User user = this.userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(loginRequest.username()))
                .findFirst()
                .orElseThrow((() -> new UserNotFoundException(
                        "사용자 이름이 " + loginRequest.username() + "인 사용자를 찾을 수 없습니다.")));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new IllegalArgumentException(
                    "입력하신 비밀번호 " + loginRequest.password() + "가 옳바르지 않습니다.");
        }

        return this.authMapper.toLoginResponse(user);
    }
}
