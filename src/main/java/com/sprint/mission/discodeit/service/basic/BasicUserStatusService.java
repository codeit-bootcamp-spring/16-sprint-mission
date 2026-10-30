package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
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
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId()))
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        if (userStatusRepository.findByUserId(request.userId()).isPresent())
            throw new IllegalArgumentException("이미 해당 사용자의 접속 상태가 존재합니다.");

        Instant lastActiveAt = request.lastActiveAt() != null ? request.lastActiveAt() : Instant.now();
        UserStatus userStatus = userStatusRepository.save(
                new UserStatus(request.userId(), lastActiveAt)
        );
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto findById(UUID id) {
        UserStatus userStatus = getUserStatusOrThrow(id);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    public UserStatusDto update(UUID id, UserStatusUpdateRequest request) {
        return applyUdpate(request, getUserStatusOrThrow(id));
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 접속 상태가 존재하지 않습니다."));

        return applyUdpate(request, userStatus);
    }

    @Override
    public void delete(UUID id) {
        getUserStatusOrThrow(id);
        userStatusRepository.deleteById(id);
    }

    private UserStatus getUserStatusOrThrow(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 접속 상태입니다."));
    }

    private UserStatusDto applyUdpate(UserStatusUpdateRequest request, UserStatus userStatus) {
        Instant lastActiveAt = request.newLastActiveAt() != null ? request.newLastActiveAt() : Instant.now();
        userStatus.updateLastActiveAt(lastActiveAt);

        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }
}
