package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto login(AuthLoginRequestDto dto) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(dto.getUsername()))
                .filter(u -> u.getPassword().equals(dto.getPassword()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.")
                );

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileId(user.getProfileId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
