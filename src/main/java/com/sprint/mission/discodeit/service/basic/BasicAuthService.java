package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponseDto login(AuthLoginRequestDto dto) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(dto.getUsername()))
                .findFirst()
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, dto.getUsername())
                );

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD);
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND)
                );

        userStatus.updateLastActiveAt();
        userStatusRepository.save(userStatus);

        return toResponseDto(user);
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .profileId(user.getProfileId())
                .build();
    }
}
