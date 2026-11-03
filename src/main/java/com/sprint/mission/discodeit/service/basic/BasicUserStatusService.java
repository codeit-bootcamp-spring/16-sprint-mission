
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        if (request == null || request.userId() == null
                || request.lastActiveAt() == null) {
            throw new IllegalArgumentException("필수 정보가 없습니다.");
        }

        boolean userExists = userRepository.getAll().stream()
                .anyMatch(user -> user.getId().equals(request.userId()));

        if (!userExists) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new IllegalArgumentException("이미 접속 상태가 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(
                request.userId(),
                request.lastActiveAt()
        );

        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.getById(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.getAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.getById(request.id());

        if (userStatus == null) {
            throw new IllegalArgumentException("접속 상태를 찾을 수 없습니다.");
        }

        userStatus.update(request.lastActiveAt());
        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public UserStatus updateByUserId(UserStatusUpdateByUserIdRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("접속 상태를 찾을 수 없습니다."));

        userStatus.update(request.lastActiveAt());
        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }
}
