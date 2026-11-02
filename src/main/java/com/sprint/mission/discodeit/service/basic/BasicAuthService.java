package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    // 로그인
    @Override
    public User login(LoginRequest request) {

        // 사용자 이름으로 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자 이름 또는 비밀번호가 올바르지 않습니다."
                        ));

        // 비밀번호 확인
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException(
                    "사용자 이름 또는 비밀번호가 올바르지 않습니다."
            );
        }

        // 로그인 성공 시 사용자 반환
        return user;
    }
}