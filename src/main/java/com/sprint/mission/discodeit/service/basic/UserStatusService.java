package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.*;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UserStatusResponse create(UserStatusCreateRequest createRequest) {
        UUID userId = createRequest.userId();
        Instant lastActiveTime = createRequest.lastActiveTime();

        this.userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 접속 상태를 생성할 유저가 존재하지 않습니다."));
        if (this.userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("유저 접속 상태가 이미 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(userId, lastActiveTime);
        this.userStatusRepository.save(userStatus);
        return UserStatusResponse.from(userStatus);
    }

    public UserStatusResponse findById(UUID id) {
        UserStatus userStatus = this.userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("유저 접속 상태가 존재하지 않습니다."));
        return UserStatusResponse.from(userStatus);
    }

    public UserStatusResponse findByUserId(UUID userId) {
        UserStatus userStatus = this.userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 접속 상태가 존재하지 않습니다."));
        return UserStatusResponse.from(userStatus);
    }

    public List<UserStatusResponse> findAll() {
        return this.userStatusRepository.findAll()
                .stream()
                .map(UserStatusResponse::from)
                .toList();
    }

    public UserStatusResponse update(UUID id, UserStatusUpdateRequest updateRequest) {
        Instant lastActiveTime = updateRequest.newLastActiveAt();

        UserStatus userStatus = this.userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("유저 접속 상태가 존재하지 않습니다."));
        this.userRepository.findById(userStatus.getUserId())
                .orElseThrow(() -> new NoSuchElementException("수정할 유저 접속 상태의 유저가 존재하지 않습니다."));
        userStatus.update(lastActiveTime);

        this.userStatusRepository.update(id, userStatus);
        return UserStatusResponse.from(userStatus);
    }

    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest updateRequest) {
        Instant lastActiveTime = updateRequest.newLastActiveAt();

        this.userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("수정할 유저 접속 상태의 유저가 존재하지 않습니다."));
        UserStatus userStatus = this.userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 접속 상태가 존재하지 않습니다."));
        userStatus.update(lastActiveTime);

        this.userStatusRepository.update(userStatus.getId(), userStatus);
        return UserStatusResponse.from(userStatus);
    }

    public void delete(UUID id) {
        UserStatus userStatus = this.userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("유저 접속 상태가 존재하지 않습니다."));
        userStatus.validateNotDeleted();

        this.userStatusRepository.delete(id);
    }
}
