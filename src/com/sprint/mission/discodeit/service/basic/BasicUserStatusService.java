package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        if (request == null || request.userId() == null) {
            throw new IllegalArgumentException("사용자 접속 상태 생성 정보는 필수입니다.");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        Optional<UserStatus> existingStatus =
                userStatusRepository.findByUserId(user.getId());

        if (existingStatus.isPresent()) {
            throw new IllegalArgumentException("이미 해당 사용자의 접속 상태가 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(user.getId());

        UserStatus savedUserStatus = userStatusRepository.save(userStatus);
        return toDto(savedUserStatus);
    }

    @Override
    public Optional<UserStatusDto> findById(UUID id) {
        return userStatusRepository.findById(id)
                .map(this::toDto);
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserStatusDto update(UserStatusUpdateRequest request) {
        if (request == null || request.id() == null) {
            throw new IllegalArgumentException("사용자 접속 상태 수정 정보는 필수입니다.");
        }

        UserStatus userStatus = userStatusRepository.findById(request.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자 접속 상태를 찾을 수 없습니다.")
                );

        userStatus.updateLastActiveAt(request.lastActiveAt());

        UserStatus savedUserStatus = userStatusRepository.save(userStatus);
        return toDto(savedUserStatus);
    }

    @Override
    public UserStatusDto updateByUserId(
            UUID userId,
            UserStatusUpdateRequest request
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        if (request == null || request.lastActiveAt() == null) {
            throw new IllegalArgumentException("수정할 접속 시간은 필수입니다.");
        }

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자 접속 상태를 찾을 수 없습니다.")
                );

        UserStatusUpdateRequest updateRequest =
                new UserStatusUpdateRequest(
                        userStatus.getId(),
                        request.lastActiveAt()
                );

        return update(updateRequest);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자 접속 상태를 찾을 수 없습니다.")
                );

        userStatusRepository.deleteById(id);
    }

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