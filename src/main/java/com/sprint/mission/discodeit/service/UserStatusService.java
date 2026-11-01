package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    // 사용자 상태 생성
    UserStatusDto create(UserStatusCreateRequest request);

    // 사용자 상태 단건 조회
    UserStatusDto find(UUID userStatusId);

    // 전체 사용자 상태 조회
    List<UserStatusDto> findAll();

    // 특정 사용자의 상태 조회
    UserStatusDto findByUserId(UUID userId);

    // 사용자 상태 수정
    UserStatusDto update(UserStatusUpdateRequest request);

    // 특정 사용자의 상태 수정
    UserStatusDto updateByUserId(
            UUID userId,
            UserStatusUpdateRequest request
    );

    // 사용자 상태 삭제
    void delete(UUID userStatusId);
}