package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.AuthUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthUserDto login(AuthLoginRequest request) {
        User user = userRepository.findAll().stream()
                .filter(candidate -> candidate.getUsername().equals(request.username()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with username " + request.username() + " not found"
                ));

        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("Wrong password");
        }

        return new AuthUserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId()
        );
    }
}
