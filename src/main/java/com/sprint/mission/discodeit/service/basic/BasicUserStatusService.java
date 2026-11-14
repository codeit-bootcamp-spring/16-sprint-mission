package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.status.UserStatusCreateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exceptions.DuplicateResourceException;
import com.sprint.mission.discodeit.exceptions.ResourceNotFoundException;
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

        // 유저 존재 여부 확인
        userRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 동일한 유저의 유저 상태 중복 확인
        userStatusRepository.findByUserId(request.userId())
                .ifPresent(userStatus -> {
                    throw new DuplicateResourceException("이미 존재하는 유저 상태입니다.");
                });

        UserStatus userStatus = new UserStatus(
                request.userId(),
                request.lastActiveAt()
        );

        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저 상태를 찾을 수 없습니다."));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus updateByUserId(UUID userId, Instant lastActiveAt) {

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저 상태를 찾을 수 없습니다."));

        userStatus.updateLastActiveAt(lastActiveAt);

        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public void delete(UUID id) {
        find(id);
        userStatusRepository.deleteById(id);
    }
}