package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    public UserStatusService(UserStatusRepository userStatusRepository, UserRepository userRepository) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
    }

    private UserStatusResponseDto toDto(UserStatus userStatus) {
        return new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), userStatus.getUpdatedAt());
    }

    public UserStatusResponseDto create(UserStatusRequestDto userStatusRequestDto) {
        userRepository.findById(userStatusRequestDto.userId()).orElseThrow(() -> new NoSuchElementException("User can not found"));
        if (userStatusRepository.findByUserId(userStatusRequestDto.userId()).isPresent()) {
            throw new IllegalArgumentException("This UserStatus from userId already exist");
        }

        UserStatus userStatus = new UserStatus(userStatusRequestDto.userId());
        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    public UserStatusResponseDto find(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.find(userStatusId).orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));
        return toDto(userStatus);
    }

    public List<UserStatusResponseDto> findAll() {
        List<UserStatus> userStatusList = userStatusRepository.findAll();

        return userStatusList.stream()
                .map(this::toDto)
                .toList();
    }

    public UserStatusResponseDto update(UUID userStatusId, UserStatusUpdateDto userStatusUpdateDto) {
        UserStatus userStatus = userStatusRepository.find(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));

        userStatus.update(userStatusUpdateDto.lastActiveAt());
        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    public UserStatusResponseDto updateByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));

        userStatus.updateLastAccess();
        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.find(userStatusId).orElseThrow(() -> new NoSuchElementException("UserStatus can not found"));
        userStatusRepository.deleteById(userStatusId);
    }
}
