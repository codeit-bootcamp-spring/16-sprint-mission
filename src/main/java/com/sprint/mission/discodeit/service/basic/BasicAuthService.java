package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.authentication.InvalidCredentialException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(loginRequest.username())
                        && u.getPassword().equals(loginRequest.password()))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialException(
                        "입력하신 ID 또는 Password 가 올바르지 않습니다."
                ));

        return user;
    }
}
