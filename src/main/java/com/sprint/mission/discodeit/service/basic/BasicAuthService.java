package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    /*
    public BasicAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
     */

    /* DTO 사용 전
    public User login(String username, String password) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username)
                        && u.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("일치하는 사용자를 찾을 수 없습니다."));

        return user;
    }
     */

    @Override
    public User login(LoginRequest loginRequest) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(loginRequest.username())
                        && u.getPassword().equals(loginRequest.password()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("입력하신 ID 또는 Password 가 올바르지 않습니다."));

        return user;
    }
}
