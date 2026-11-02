package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public AuthService(UserRepository userRepository, UserStatusRepository userStatusRepository) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
    }

    public UserResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.userName())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!user.getPassword().equals(loginRequestDto.password())) {
            throw new NoSuchElementException("Wrong password");
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));

        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getProfileId(), userStatus.isOnline());
    }
}
