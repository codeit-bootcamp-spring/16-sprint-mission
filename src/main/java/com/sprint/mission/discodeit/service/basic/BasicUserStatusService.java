package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    if (!userRepository.existsById(request.userId()))
      throw new UserNotFoundException("존재하지 않는 사용자입니다.");
    if (userStatusRepository.findByUserId(request.userId()).isPresent())
      throw new DuplicateUserStatusException("이미 존재하는 접속 상태입니다.");

    Instant lastActiveAt = request.lastActiveAt() != null ? request.lastActiveAt() : Instant.now();
    UserStatus userStatus =
        userStatusRepository.save(new UserStatus(request.userId(), lastActiveAt));
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto findById(UUID id) {
    UserStatus userStatus = getUserStatusOrThrow(id);
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream().map(userStatusMapper::toDto).toList();
  }

  @Override
  public UserStatusDto update(UUID id, UserStatusUpdateRequest request) {
    return applyUpdate(request, getUserStatusOrThrow(id));
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus userStatus =
        userStatusRepository
            .findByUserId(userId)
            .orElseThrow(() -> new UserStatusNotFoundException("존재하지 않는 접속 상태입니다."));

    return applyUpdate(request, userStatus);
  }

  @Override
  public void delete(UUID id) {
    getUserStatusOrThrow(id);
    userStatusRepository.deleteById(id);
  }

  private UserStatus getUserStatusOrThrow(UUID id) {
    return userStatusRepository
        .findById(id)
        .orElseThrow(() -> new UserStatusNotFoundException("존재하지 않는 접속 상태입니다."));
  }

  private UserStatusDto applyUpdate(UserStatusUpdateRequest request, UserStatus userStatus) {
    Instant lastActiveAt =
        request.newLastActiveAt() != null ? request.newLastActiveAt() : Instant.now();
    userStatus.updateLastActiveAt(lastActiveAt);

    userStatusRepository.save(userStatus);
    return userStatusMapper.toDto(userStatus);
  }
}
