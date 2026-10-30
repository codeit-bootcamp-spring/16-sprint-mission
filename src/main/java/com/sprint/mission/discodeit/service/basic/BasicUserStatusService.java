package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusByUserIdRequest;
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
    public UserStatus create(CreateUserStatusRequest request) {
        if (request == null
                || userRepository.findById(request.userId()) == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        if (userStatusRepository.findByUserId(request.userId()) != null) {
            throw new IllegalArgumentException(
                    "이미 접속 상태가 존재합니다."
            );
        }

        UserStatus status = new UserStatus(
                request.userId(),
                request.lastSeenAt() == null
                        ? Instant.now()
                        : request.lastSeenAt()
        );

        return userStatusRepository.save(status);
    }

    @Override
    public UserStatus findById(UUID id) {
        UserStatus status = userStatusRepository.findById(id);

        if (status == null) {
            throw new IllegalArgumentException(
                    "접속 상태를 찾을 수 없습니다."
            );
        }

        return status;
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UpdateUserStatusRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "수정 정보가 없습니다."
            );
        }

        UserStatus status = findById(request.id());

        if (request.lastSeenAt() == null) {
            throw new IllegalArgumentException(
                    "접속 시간이 없습니다."
            );
        }

        status.update(request.lastSeenAt());

        return userStatusRepository.save(status);
    }

    @Override
    public UserStatus updateByUserId(
            UpdateUserStatusByUserIdRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "수정 정보가 없습니다."
            );
        }

        UserStatus status =
                userStatusRepository.findByUserId(request.userId());

        if (status == null) {
            throw new IllegalArgumentException(
                    "사용자 접속 상태를 찾을 수 없습니다."
            );
        }

        if (request.lastSeenAt() == null) {
            throw new IllegalArgumentException(
                    "접속 시간이 없습니다."
            );
        }

        status.update(request.lastSeenAt());

        return userStatusRepository.save(status);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        userStatusRepository.delete(id);
    }
}