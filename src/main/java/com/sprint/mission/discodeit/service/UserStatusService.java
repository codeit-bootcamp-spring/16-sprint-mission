package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    public UserStatusService(UserStatusRepository userStatusRepository, UserRepository userRepository) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
    }

    public UserStatusResponseDto create(UserStatusRequestDto userStatusRequestDto) {
        userRepository.findById(userStatusRequestDto.userId()).orElseThrow(() -> new NoSuchElementException("User can not found"));
        if (userStatusRepository.findByUserId(userStatusRequestDto.userId()) != null) {
            throw new IllegalArgumentException("This UserStatus from userId already exist");
        }

        UserStatus userStatus = new UserStatus(userStatusRequestDto.userId());
        userStatusRepository.save(userStatus);

        return new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), userStatus.getUpdatedAt());
    }

    public UserStatusResponseDto find(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.find(userStatusId).orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));
        return new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), userStatus.getUpdatedAt());
    }

    public List<UserStatusResponseDto> findAll(UUID userStatusId) {
        List<UserStatus> userStatusList = userStatusRepository.findAll(userStatusId);
        List<UserStatusResponseDto> userStatusResponseDtos = new ArrayList<>();
        for (UserStatus userStatus : userStatusList) {
            userStatusResponseDtos.add(new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), userStatus.getUpdatedAt()));
        }
        return userStatusResponseDtos;
    }

    public UserStatusResponseDto update(UUID userStatusId, UserStatusRequestDto userStatusRequestDto) {
        UserStatus userStatus = userStatusRepository.find(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));

        userStatus.update(userStatusRequestDto.userId());
        userStatusRepository.save(userStatus);

        return new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), userStatus.getUpdatedAt());
    }

    public UserStatusResponseDto updateByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));

        userStatus.updateLastAccess();
        userStatusRepository.save(userStatus);

        return new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), userStatus.getUpdatedAt());
    }

    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.find(userStatusId).orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));
        userStatusRepository.deleteById(userStatusId);
    }
}
