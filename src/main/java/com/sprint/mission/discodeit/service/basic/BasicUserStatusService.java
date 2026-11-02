package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
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
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserStatusResponse create(CreateUserStatusRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("해당 id의 유저는 존재하지 않음");
        }

        if (userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new IllegalStateException("해당 id의 user status는 이미 존재함");
        }

        UserStatus newStatus = new UserStatus(request.userId());
        userStatusRepository.save(newStatus);

        return UserStatusResponse.from(newStatus);
    }

    @Override
    public UserStatusResponse find(UUID statusId) {
        UserStatus userStatus = userStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 user status는 존재하지 않음"));

        return UserStatusResponse.from(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusResponse::from)
                .toList();
    }

    @Override
    public UserStatusResponse update(UUID statusId, UpdateUserStatusRequest request) {
        if (request.lastActiveAt() == null) {
            throw new IllegalArgumentException("최근 활동 시간에 적절치 못한 값이 들어옴");
        }

        UserStatus userStatus = userStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 user status는 존재하지 않음"));

        userStatus.update(request.lastActiveAt());
        userStatusRepository.save(userStatus);
        return UserStatusResponse.from(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UpdateUserStatusRequest request) {
        if (request.lastActiveAt() == null) {
            throw new IllegalArgumentException("최근 활동 시간에 적절치 못한 값이 들어옴");
        }

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 id의 유저는 존재하지 않음");
        }

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 user status는 존재하지 않음"));

        userStatus.update(request.lastActiveAt());
        userStatusRepository.save(userStatus);
        return UserStatusResponse.from(userStatus);
    }

    @Override
    public void delete(UUID statusId) {
        UserStatus userStatus = userStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 user status는 존재하지 않음"));

        if (userRepository.existsById(userStatus.getUserId())) {
            throw new IllegalStateException("해당 user status에 매핑된 User가 존재하므로 삭제할 수 없음");
        }

        userStatusRepository.deleteById(statusId);
    }
}
