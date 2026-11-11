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
        userRepository.findById(request.userId()).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        for (UserStatus userStatus : userStatusRepository.findAll()) {
            if (userStatus.getUserId().equals(request.userId())) {
                throw new IllegalArgumentException("이미 접속 상태가 존재하는 사용자");
            }
        }

        UserStatus userStatus = new UserStatus(
                request.userId(), request.lastActiveAt());

        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public UserStatus findById(UUID id) {
        return userStatusRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("접속 상태를 찾을 수 없음"));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(
                request.userStatusId()).orElseThrow(() ->
                new IllegalArgumentException("접속 상태를 찾을 수 없음"));

        userStatus.update(request.lastActiveAt());
        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public UserStatus updateByUserId(UUID userId, Instant lastActiveAt) {
        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("접속 상태를 찾을 수 없음"));

        userStatus.update(lastActiveAt);
        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("접속 상태를 찾을 수 없음"));

        userStatusRepository.deleteById(id);
    }
}