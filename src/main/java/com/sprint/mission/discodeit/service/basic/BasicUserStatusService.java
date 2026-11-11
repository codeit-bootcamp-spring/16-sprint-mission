package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
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
    public UserStatus create(UserStatusCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("사용자 상태 생성 요청은 null일 수 없습니다.");
        }
        if (request.userId() == null || request.lastActiveAt() == null) {
            throw new IllegalArgumentException("사용자 ID와 마지막 접속 시각은 필수입니다.");
        }

        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "사용자를 찾을 수 없습니다: " + request.userId()));

        if (userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new IllegalArgumentException("해당 사용자의 상태가 이미 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(request.userId(), request.lastActiveAt());
        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public UserStatus findById(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "사용자 상태를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public void update(UserStatusUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("사용자 상태 수정 요청은 null일 수 없습니다.");
        }
        if (request.id() == null || request.lastActiveAt() == null) {
            throw new IllegalArgumentException("사용자 상태 ID와 마지막 접속 시각은 필수입니다.");
        }

        UserStatus userStatus = findById(request.id());
        userStatus.updateLastActiveAt(request.lastActiveAt());
        userStatusRepository.save(userStatus);
    }

    @Override
    public void updateByUserId(UUID userId, Instant lastActiveAt) {
        if (userId == null || lastActiveAt == null) {
            throw new IllegalArgumentException("사용자 ID와 마지막 접속 시각은 필수입니다.");
        }

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "사용자 상태를 찾을 수 없습니다. userId: " + userId));
        update(new UserStatusUpdateRequest(userStatus.getId(), lastActiveAt));
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        if (!userStatusRepository.deleteById(id)) {
            throw new IllegalArgumentException("사용자 상태를 찾을 수 없습니다: " + id);
        }
    }
}
