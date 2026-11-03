package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
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

    @Override
    public UserStatus create(UserStatusCreateDto dto) {
        if (!userRepository.existsById(dto.userId())) {
            throw new NoSuchElementException("User not found with id " + dto.userId());
        }

        boolean alreadyExists = userStatusRepository.findByUserId(dto.userId()).isPresent();
        if (alreadyExists) {
            throw new IllegalArgumentException("해당 유저의 상태 정보가 이미 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(dto.userId());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateDto dto) {
        UserStatus userStatus = userStatusRepository.findById(dto.id())
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + dto.id() + " not found"));

        userStatus.updateLastSeen();
        return userStatusRepository.save(userStatus);
    }
    @Override
    public UserStatus updateByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id " + userId);
        }
        // 해당 유저 ID를 가진 유저 상태 조회
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for userId " + userId));

        //최근 활동 시간 정보(lastSeenAt)를 현재 시간으로 업데이트
        userStatus.updateLastSeen();
        return userStatusRepository.save(userStatus);
    }
    @Override
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));

        userStatusRepository.delete(userStatus);
    }
}
