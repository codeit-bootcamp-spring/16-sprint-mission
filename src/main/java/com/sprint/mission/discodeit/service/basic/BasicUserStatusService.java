package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    // 사용자 상태 생성
    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {

        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException(
                    "User not found with id " + request.getUserId()
            );
        }

        if (userStatusRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException(
                    "UserStatus already exists"
            );
        }

        UserStatus userStatus = new UserStatus(request.getUserId());

        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    // 사용자 상태 단건 조회
    @Override
    public UserStatusDto find(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus not found with id " + userStatusId
                ));

        return toDto(userStatus);
    }

    // 전체 사용자 상태 조회
    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // 특정 사용자의 상태 조회
    @Override
    public UserStatusDto findByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus not found for user " + userId
                ));

        return toDto(userStatus);
    }

    // 사용자 상태 수정
    @Override
    public UserStatusDto update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository
                .findById(request.getUserStatusId())
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus not found with id "
                                + request.getUserStatusId()
                ));

        userStatus.update(request.getLastActiveAt());

        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    // 특정 사용자의 상태 수정
    @Override
    public UserStatusDto updateByUserId(
            UUID userId,
            UserStatusUpdateRequest request
    ) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus not found for user " + userId
                ));

        userStatus.update(request.getLastActiveAt());

        userStatusRepository.save(userStatus);

        return toDto(userStatus);
    }

    // 사용자 상태 삭제
    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException(
                    "UserStatus not found with id " + userStatusId
            );
        }

        userStatusRepository.deleteById(userStatusId);
    }

    // Entity를 DTO로 변환
    private UserStatusDto toDto(UserStatus userStatus) {
        return new UserStatusDto(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId(),
                userStatus.getLastActiveAt(),
                userStatus.isOnline()
        );
    }
}