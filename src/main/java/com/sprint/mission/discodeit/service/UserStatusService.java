package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.status.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.status.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    // 유저 상태 생성
    UserStatus create(UserStatusCreateRequest request);

    // ID로 유저 상태 단건 조회
    UserStatus find(UUID id);

    // 모든 유저 상태 조회
    List<UserStatus> findAll();

    // 유저 상태 수정
    UserStatus update(UserStatusUpdateRequest request);

    // 특정 유저의 유저 상태 수정
    UserStatus updateByUserId(UUID userId, Instant lastActiveAt);

    // ID로 유저 상태 삭제
    void delete(UUID id);
}
