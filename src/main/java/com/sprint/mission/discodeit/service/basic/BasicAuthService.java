package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.InvalidRequestException;
import com.sprint.mission.discodeit.exception.LoginFailedException;
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
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findAll().stream()
                .filter(candidate -> candidate.getName().equals(request.username()))
                .findFirst().orElseThrow(() ->
                        new ResourceNotFoundException("유저를 찾을 수 없음"));

        if (request.password() == null || !request.password().equals(user.getPassword())) {
            throw new InvalidRequestException("비밀번호가 일치하지 않습니다");
        }

        return new UserResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getName(), user.getEmail(), user.getProfileId());
    }

}
