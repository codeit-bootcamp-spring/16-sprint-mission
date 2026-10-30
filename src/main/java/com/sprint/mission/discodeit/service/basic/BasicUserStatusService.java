package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponseDto createUserStatus(UserStatusCreateRequestDto dto) {
        UUID userId = dto.getUserId();

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다."));

        userStatusRepository.findByUserId(userId)
                .ifPresent(userStatus -> {
                    throw new IllegalArgumentException("이미 해당 사용자의 상태가 존재합니다.");
                });

        UserStatus userStatus = new UserStatus(userId);
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        return toResponseDto(savedUserStatus);
    }

    @Override
    public UserStatusResponseDto findUserStatus(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자 상태입니다."));

        return toResponseDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAllUserStatuses() {
        return userStatusRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public UserStatusResponseDto updateUserStatus(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("존재하지 않는 사용자 상태입니다."));

        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        return toResponseDto(savedUserStatus);
    }

    @Override
    public UserStatusResponseDto updateUserStatusByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 사용자의 상태가 존재하지 않습니다."));

        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        return toResponseDto(savedUserStatus);
    }

    @Override
    public void deleteUserStatus(UUID userStatusId) {
        userStatusRepository.findById(userStatusId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자 상태입니다."));

        userStatusRepository.deleteById(userStatusId);
    }

    private UserStatusResponseDto toResponseDto(UserStatus userStatus) {
        return UserStatusResponseDto.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUserId())
                .lastActiveAt(userStatus.getLastActiveAt())
                .createdAt(userStatus.getCreatedAt())
                .updatedAt(userStatus.getUpdatedAt())
                .online(userStatus.isOnline())
                .build();
    }
}
