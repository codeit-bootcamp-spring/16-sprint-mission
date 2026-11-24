package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import com.sprint.mission.discodeit.exception.ResourceNotFoundException;

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
                || request.username().isBlank()
                || request.password() == null
                || request.password().isBlank()) {

            throw new IllegalArgumentException(
                    "아이디와 비밀번호를 입력하세요."
            );
        }

        User user = userRepository.findByUsername(
                request.username()
        );

        if (user == null) {
            throw new ResourceNotFoundException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        if (!user.getPassword().equals(
                request.password()
        )) {
            throw new IllegalArgumentException(
                    "비밀번호가 일치하지 않습니다."
            );
        }

        UserStatus userStatus =
                userStatusRepository.findByUserId(
                        user.getId()
                );

        boolean online = userStatus != null
                && userStatus.isOnline();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                online,
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}