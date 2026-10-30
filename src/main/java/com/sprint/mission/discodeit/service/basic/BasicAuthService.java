package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        if (request == null
                || request.username() == null
                || request.password() == null) {
            throw new IllegalArgumentException(
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );
        }

        User user = userRepository.findByUsername(
                request.username()
        );

        if (user == null
                || !user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException(
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );
        }

        UserStatus status =
                userStatusRepository.findByUserId(user.getId());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                status != null && status.isOnline(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}