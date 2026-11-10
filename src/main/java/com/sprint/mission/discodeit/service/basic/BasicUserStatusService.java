package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
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
    public UserStatusResponse create(UserStatusCreateRequest request) {
        this.userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("ID가 " + request.userId() + "인 사용자를 찾을 수 없습니다."));

        if (this.userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new DuplicateResourceException(
                    "해당 사용자의 온라인 상태 정보가 이미 존재합니다. 사용자 ID: " + request.userId()
            );
        }

        UserStatus userStatus =  new UserStatus(request.userId());
        this.userStatusRepository.save(userStatus);

        return this.toUserStatusResponse(userStatus);
    }

    private UserStatusResponse toUserStatusResponse(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt()
        );
    }

    @Override
    public UserStatusResponse find(UUID userStatusId) {
        UserStatus userStatus = this.userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new UserStatusNotFoundException(
                        "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다."
                ));

        return this.toUserStatusResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        List<UserStatus> userStatuses = this.userStatusRepository.findAll();

        return userStatuses.stream()
                .map(this::toUserStatusResponse)
                .toList();
    }

    @Override
    public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = this.userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new UserStatusNotFoundException(
                                "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다."
                        ));

        userStatus.update();
        this.userStatusRepository.save(userStatus);
        return this.toUserStatusResponse(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId) {
        this.userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(
                                "ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UserStatus userStatus = this.userStatusRepository.findByUserId(userId)
                        .orElseThrow(() -> new UserStatusNotFoundException(
                                        "사용자 ID가 " + userId + "인 사용자의 상태 정보를 찾을 수 없습니다."
                                ));

        userStatus.update();
        this.userStatusRepository.save(userStatus);
        return this.toUserStatusResponse(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (this.userStatusRepository.existsById(userStatusId)) {
            throw new UserStatusNotFoundException(
                    "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다.");
        }
        this.userStatusRepository.deleteById(userStatusId);
    }

}
