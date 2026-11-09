package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;

import com.sprint.mission.discodeit.entity.UserStatus;

import com.sprint.mission.discodeit.exception.ResourceNotFoundException;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(
            CreateUserStatusRequest request
    ) {
        if (request == null || request.userId() == null) {
            throw new IllegalArgumentException(
                    "사용자 ID가 없습니다."
            );
        }

        if (userRepository.findById(request.userId()) == null) {
            throw new ResourceNotFoundException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        if (userStatusRepository.findByUserId(request.userId())
                != null) {
            throw new IllegalStateException(
                    "이미 존재하는 사용자 접속 상태입니다."
            );
        }

        UserStatus status = new UserStatus(
                request.userId(),
                request.lastSeenAt() == null
                        ? Instant.now()
                        : request.lastSeenAt()
        );

        return toResponse(userStatusRepository.save(status));
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        return toResponse(requireUserStatus(id));
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserStatusResponse update(
            UpdateUserStatusRequest request
    ) {
        if (request == null || request.lastSeenAt() == null) {
            throw new IllegalArgumentException(
                    "마지막 접속 시간이 필요합니다."
            );
        }

        UserStatus status = requireUserStatus(request.id());

        status.update(request.lastSeenAt());

        return toResponse(userStatusRepository.save(status));
    }

    @Override
    public UserStatusResponse updateByUserId(
            UpdateUserStatusByUserIdRequest request
    ) {
        if (request == null
                || request.userId() == null
                || request.lastSeenAt() == null) {
            throw new IllegalArgumentException(
                    "사용자 ID와 접속 시간이 필요합니다."
            );
        }

        if (userRepository.findById(request.userId()) == null) {
            throw new ResourceNotFoundException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        UserStatus status =
                userStatusRepository.findByUserId(request.userId());

        if (status == null) {
            throw new ResourceNotFoundException(
                    "사용자 접속 상태를 찾을 수 없습니다."
            );
        }

        status.update(request.lastSeenAt());

        return toResponse(userStatusRepository.save(status));
    }

    @Override
    public void delete(UUID id) {
        requireUserStatus(id);
        userStatusRepository.delete(id);
    }

    private UserStatus requireUserStatus(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "접속 상태 ID가 없습니다."
            );
        }

        UserStatus status = userStatusRepository.findById(id);

        if (status == null) {
            throw new ResourceNotFoundException(
                    "사용자 접속 상태를 찾을 수 없습니다."
            );
        }

        return status;
    }

    private UserStatusResponse toResponse(UserStatus status) {
        return new UserStatusResponse(
                status.getId(),
                status.getUserId(),
                status.getLastSeenAt(),
                status.isOnline(),
                status.getCreatedAt(),
                status.getUpdatedAt()
        );
    }
}