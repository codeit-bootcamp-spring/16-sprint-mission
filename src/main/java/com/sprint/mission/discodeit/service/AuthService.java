package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.NoSuchElementException;

public class AuthService {
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.userName())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!user.getPassword().equals(loginRequestDto.password())) {
            throw new NoSuchElementException("Wrong password");
        }

        userRepository.save(user);
        UserStatus userStatus = new UserStatus(user.getId());
        UserResponseDto userResponseDto =
                new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getProfileId(), userStatus.isOnline());

        return userResponseDto;
    }
}
