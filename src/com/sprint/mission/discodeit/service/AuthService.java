package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public UserDto login(LoginRequest request) {
        // 1. 로그인 요청 검증
        if (request == null
                || request.username() == null
                || request.username().isBlank()
                || request.password() == null
                || request.password().isBlank()) {
            throw new IllegalArgumentException(
                    "아이디와 비밀번호를 입력해 주세요."
            );
        }

        // 2. 아이디와 비밀번호가 일치하는 사용자 조회
        User user = userRepository.findByUsername(request.username())
                .filter(existing ->
                        existing.getPassword().equals(request.password()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "아이디 또는 비밀번호가 일치하지 않습니다."
                        ));

        // 3. 사용자 접속 시간 갱신
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "사용자 접속 정보를 찾을 수 없습니다."
                        ));

        status.updateLastActiveAt(Instant.now());
        userStatusRepository.save(status);

        // 4. 비밀번호를 제외한 사용자 정보 반환
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                status.isOnline()
        );
    }
}
