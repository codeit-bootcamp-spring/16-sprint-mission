package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .filter(u -> u.getPassword().equals(request.password()))
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserStatusNotFoundException("존재하지 않는 접속 상태입니다."));
        status.updateLastActiveAt(Instant.now());
        userStatusRepository.save(status);

        return userMapper.toDto(user);
    }
}
