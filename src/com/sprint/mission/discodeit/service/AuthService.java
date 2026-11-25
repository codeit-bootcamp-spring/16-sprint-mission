package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public UserDto login(LoginRequest request) {
        // 1. 로그인 요청 검증
        if (request == null || request.username() == null || request.username().isBlank() || request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("아이디와 비밀번호를 입력해 주세요.");
        }

        // 2. 아이디로 사용자 조회
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        // 3. 비밀번호 검증
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 4. 사용자 접속 정보 조회
        UserStatus status = userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("사용자 접속 정보를 찾을 수 없습니다."));

        // 5. 사용자 접속 시간 갱신
        status.updateLastActiveAt(Instant.now());
        userStatusRepository.save(status);

        // 6. 비밀번호를 제외한 사용자 정보 반환
        return new UserDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getUsername(), user.getEmail(), user.getProfileImageId(), status.isOnline());
    }
}