package com.sprint.mission.discodeit.service.otherService;

import com.sprint.mission.discodeit.dto.request.otherRequest.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.otherRequest.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    // 읽음 상태 생성
    ReadStatus create(ReadStatusCreateRequest request);

    // ID로 읽음 상태 단건 조회
    ReadStatus find(UUID id);

    // 특정 유저의 모든 읽음 상태 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 읽음 상태 수정
    ReadStatus update(ReadStatusUpdateRequest request);

    // ID로 읽음 상태 삭제
    void delete(UUID id);
}