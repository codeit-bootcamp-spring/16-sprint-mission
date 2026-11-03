package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
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
    public UserStatusResponse create(CreateUserStatusRequest request) {
        if (request == null
                || userRepository.findById(request.userId()) == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        if (userStatusRepository.findByUserId(
                request.userId()) != null) {
            throw new IllegalArgumentException(
                    "이미 존재하는 접속 상태입니다."
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
        return toResponse(require(id));
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
                    "접속 시간을 입력하세요."
            );
        }

        UserStatus status = require(request.id());

        status.update(request.lastSeenAt());

        return toResponse(userStatusRepository.save(status));
    }

    @Override
    public UserStatusResponse updateByUserId(
            UpdateUserStatusByUserIdRequest request
    ) {
        if (request == null || request.lastSeenAt() == null) {
            throw new IllegalArgumentException(
                    "접속 시간을 입력하세요."
            );
        }

        UserStatus status =
                userStatusRepository.findByUserId(request.userId());

        if (status == null) {
            throw new IllegalArgumentException(
                    "접속 상태를 찾을 수 없습니다."
            );
        }

        status.update(request.lastSeenAt());

        return toResponse(userStatusRepository.save(status));
    }

    @Override
    public void delete(UUID id) {
        require(id);
        userStatusRepository.delete(id);
    }

    private UserStatus require(UUID id) {
        UserStatus status = userStatusRepository.findById(id);

        if (status == null) {
            throw new IllegalArgumentException(
                    "접속 상태를 찾을 수 없습니다."
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