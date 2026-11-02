package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.Auth.AuthUserRequest;
import com.sprint.mission.discodeit.dto.Auth.AuthUserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public AuthUserResponse login(AuthUserRequest authRequest) {
        String name = authRequest.name();
        String password = authRequest.password();

        User user = this.userRepository.findByName(name).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 유저 이름입니다."));
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저 접속 기록이 존재하지 않습니다."));

        userStatus.update(Instant.now());
        this.userStatusRepository.update(userStatus.getId(), userStatus);

        return AuthUserResponse.from(user);
    }
}
